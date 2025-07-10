package org.csu.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.csu.exam.clients.AIClient;
import org.csu.exam.clients.CoursesClient;
import org.csu.exam.entity.po.Answer;
import org.csu.exam.entity.po.StudentPapers;
import org.csu.exam.entity.po.TestPapers;
import org.csu.exam.entity.vo.EventTodoVO;
import org.csu.exam.entity.vo.ExamAnalysisVO;
import org.csu.exam.entity.vo.ExamListVO;
import org.csu.exam.entity.vo.ExamVO;
import org.csu.exam.repository.TestPapersRepository;
import org.csu.exam.service.IAnswerService;
import org.csu.exam.service.IStudentPapersService;
import org.csu.exam.service.TestPapersService;
import org.csu.exception.CommonException;
import org.csu.exception.ExamException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 试卷服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestPapersServiceImpl implements TestPapersService {
    private final TestPapersRepository testPapersRepository;
    private final IStudentPapersService spsService;
    private final IAnswerService answerService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AIClient aiClient;
    private final CoursesClient coursesClient;

    /**
     * 根据ID获取试卷
     *
     * @param id 试卷ID
     * @return 试卷实体
     * @throws CommonException 当试卷不存在时抛出异常
     */
    @Override
    public ExamVO getTestPapersById(String id, long userId) {
        log.info("开始获取试卷，试卷ID: {}, 用户ID: {}", id, userId);
        TestPapers paper = _getTestPapersById(id, userId);
        String courseName = coursesClient.getCourseName(paper.getCourseId()).getData();
        // TODO 非测试删除该行
        paper.setStartTime(new Date(System.currentTimeMillis()));
        // 未开始
        if (paper.getStartTime().getTime() > System.currentTimeMillis()) {
            log.info("试卷未开始，试卷ID: {}", id);
            paper.setQuestions(null);
            return ExamVO.builder().paper(paper)
                    .answers(null)
                    .submitTime(null)
                    .submit(false).start(false)
                    .courseName(courseName)
                    .build();
        }
        // 已提交
        List<Answer> answers = getPaperAnswersFromStudent(id, userId);
        StudentPapers sp = spsService.getByUserIdAndPaperId(userId, id);
        LocalDateTime submitTime = sp.getSubmitTime();
        // 如果时间到了但未提交
        if (submitTime == null && paper.getStartTime().getTime() + (long) paper.getDuration() * 60 * 1000 <= System.currentTimeMillis()) {
            Instant instant = Instant.ofEpochMilli(paper.getStartTime().getTime() + (long) paper.getDuration() * 60 * 1000);
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");
            submitTime = LocalDateTime.ofInstant(instant, zoneId);
            sp.setSubmitTime(submitTime);
            sp.setStatus(1);
            spsService.saveOrUpdate(sp);
        }
        if (submitTime != null) {
            log.info("试卷已提交，试卷ID: {}, 用户ID: {}", id, userId);
            return ExamVO.builder().paper(paper).answers(answers)
                    .submitTime(submitTime)
                    .submit(true).start(true)
                    .courseName(courseName)
                    .totalScore(sp.getTotalScore())
                    .build();
        }
        // 开始进行
        log.info("试卷进行中，试卷ID: {}, 用户ID: {}", id, userId);
        paper.getQuestions().forEach(question -> {
            question.setAnalysis(null);
            question.setAnswer(null);
            question.setTags(null);
            question.setDifficulty(null);
        });
        return ExamVO.builder().paper(paper).answers(answers)
                .submitTime(null)
                .submit(false).start(true)
                .courseName(courseName)
                .build();
    }

    /**
     * 判断题目是否存在
     *
     * @param paperId    试卷ID
     * @param questionId 题目ID
     * @return true：存在；false：不存在
     */
    @Override
    public boolean existQuestion(String paperId, String questionId) {
        log.info("检查题目是否存在，试卷ID: {}, 题目ID: {}", paperId, questionId);
        ObjectId id = new ObjectId(paperId);
        return testPapersRepository.existQuestion(id, questionId) != null;
    }

    /**
     * 提交试卷
     *
     * @param id     试卷ID
     * @param userId 用户ID
     * @return true：提交成功；false：提交失败
     */
    @Override
    public boolean submitPaper(String id, long userId) {
        log.info("开始提交试卷，试卷ID: {}, 用户ID: {}", id, userId);
        if (!spsService.exists(userId, id)) {
            log.error("提交试卷失败，不相关的试卷和学生，试卷ID: {}, 用户ID: {}", id, userId);
            throw new ExamException("不相关的试卷和学生");
        }
        boolean result = spsService.updateStatus(userId, id, 1);
        log.info("提交试卷结果: {}, 试卷ID: {}, 用户ID: {}", result, id, userId);
        return result;
    }

    /**
     * @param paperId 试卷ID
     * @param userId  用户ID
     * @return
     */
    @Override
    public List<Answer> getPaperAnswersFromStudent(String paperId, long userId) {
        log.info("获取学生试卷答案，试卷ID: {}, 用户ID: {}", paperId, userId);
        return answerService.getAnswersByPaperAndUser(paperId, userId);
    }

    /**
     * 保存学生对于试卷的回答
     *
     * @param answer 学生对于试卷的回答
     * @param userId 用户ID
     * @return
     */
    @Override
    public boolean saveOrUpdateQuestionAnswer(Answer answer, long userId) {
        log.info("保存或更新问题答案，试卷ID: {}, 问题ID: {}, 用户ID: {}", answer.getPaperId(), answer.getQuestionId(), userId);
        // 判断paper_id,question_id,user_id是否为空
        if (answer.getPaperId() == null || answer.getQuestionId() == null || userId == 0) {
            log.error("保存答案失败，参数为空，试卷ID: {}, 问题ID: {}, 用户ID: {}", answer.getPaperId(), answer.getQuestionId(), userId);
            throw new ExamException("试卷ID或问题ID或用户ID为空");
        }
        // 判断试卷id和问题id是否存在
        _getTestPapersById(answer.getPaperId(), userId);
        // 判断问题id是否存在
        if (!existQuestion(answer.getPaperId(), answer.getQuestionId())) {
            log.error("保存答案失败，问题不存在，试卷ID: {}, 问题ID: {}", answer.getPaperId(), answer.getQuestionId());
            throw new ExamException("该试卷上不存在该问题");
        }
        boolean result = answerService.saveOrUpdate(answer, userId);
        log.info("保存答案结果: {}, 试卷ID: {}, 问题ID: {}, 用户ID: {}", result, answer.getPaperId(), answer.getQuestionId(), userId);
        return result;
    }

    /**
     * 更新学生分数
     *
     * @param userId
     * @param paperId
     * @param totalScore
     */
    @Override
    @Transactional
    public boolean updateScore(long userId, String paperId, double totalScore, List<Answer> answers) {
        log.info("更新学生分数，试卷ID: {}, 用户ID: {}, 分数: {}", paperId, userId, totalScore);
        boolean a = spsService.updateScore(userId, paperId, totalScore);
        answers.forEach(answer -> answer.setUserId(userId));
        boolean b = answerService.saveAnswers(answers);
        log.info("更新学生分数结果: {}, 试卷ID: {}, 用户ID: {}, 分数: {}", a && b, paperId, userId, totalScore);
        return a && b;
    }

    /**
     * 获取AI题目分析
     *
     * @param id         试卷ID
     * @param questionId questionId
     * @param userId     用户id
     * @return
     */
    @Override
    public Flux<String> getAIAnalysis(String id, String questionId, long userId) {
        log.info("获取AI题目分析，试卷ID: {}, 问题ID: {}, 用户ID: {}", id, questionId, userId);
        //比对用户id和试卷id是否有关联
        if (!spsService.exists(userId, id)) {
            log.error("获取AI题目分析失败，不相关的试卷和学生，试卷ID: {}, 用户ID: {}", id, userId);
            throw new ExamException("不相关的试卷和学生");
        }
        TestPapers papers = testPapersRepository.existQuestion(new ObjectId(id), questionId);
        if (papers == null) {
            log.error("获取AI题目分析失败，问题不存在，试卷ID: {}, 问题ID: {}", id, questionId);
            throw new ExamException("该试卷上不存在该问题");
        }
        TestPapers.Question question = papers.getQuestions().stream()
                .filter(q -> q.getQuestionId()
                        .equals(questionId)).findFirst().orElseThrow(
                        () -> new ExamException("该试卷上不存在该问题")
                );
        //获取题目分析
        String prompt = JSONObject.toJSONString(question);
        return aiClient.analysis(prompt);
    }

    @Override
    public TestPapers.Question getQuestionById(String id, String questionId, long userId) {
        log.info("获取题目，试卷ID: {}, 问题ID: {}, 用户ID: {}", id, questionId, userId);
        TestPapers papers = _getTestPapersById(id, userId);
        return papers.getQuestions().stream()
                .filter(q -> q.getQuestionId()
                        .equals(questionId)).findFirst().orElseThrow(
                        () -> new ExamException("该试卷上不存在该问题")
                );
    }

    /**
     * 获取考试列表
     *
     * @param userId 学生id
     * @return
     */
    @Override
    public Map<String, Object> getExamList(long userId, int page, int size, String courseName, String status, String title, Long courseId) {
        // 获取学生考试对象
        List<StudentPapers> sps = spsService.getExamIdsByUserId(userId);
        if (sps == null || sps.isEmpty()) {
            return Map.of(
                    "total", 0,
                    "list", Collections.emptyList()
            );
        }
        if (page <= 0 || size < 0) {
            return Map.of(
                    "total", 0,
                    "list", Collections.emptyList()
            );
        }
        //int total=sps.size();
        // 进行分页
        List<ExamListVO> examListVOS = new ArrayList<>();
        sps.forEach(sp -> {
            // 获取试卷信息
            TestPapers paper = _getTestPapersById(sp.getPaperId(), userId);
            // 判断课程Id
            if (courseId != null && !((Long) paper.getCourseId()).equals(courseId)) return;
            // 判断title
            if (title != null && !title.isEmpty() && !paper.getTitle().contains(title)) return;
            // 判断课程名
            String finalCourseName = coursesClient.getCourseName(paper.getCourseId()).getData();
            if (courseName != null && finalCourseName != null && !finalCourseName.contains(courseName)) {
                return;
            }
            // 判断考试状态
            String finalStatus;
            // TODO 非测试删除该行代码
            paper.setStartTime(new Date());
            if (paper.getStartTime().getTime() > System.currentTimeMillis()) {
                finalStatus = "pending";
            } else if (sp.getSubmitTime() == null) {
                finalStatus = "ongoing";
            } else {
                finalStatus = "completed";
            }
            if (status != null && !finalStatus.equals(status) && !status.isEmpty()) return;
            List<Answer> answers = answerService.getAnswersByPaperAndUser(paper.getId(), userId);
            List<TestPapers.Question> questions = paper.getQuestions();
            AtomicInteger correctNum = new AtomicInteger();
            for (Answer answer : answers) {
                questions.stream().filter(q -> q.getQuestionId().equals(answer.getQuestionId())).findFirst().ifPresent(q -> {
                    if (q.getScore() == answer.getScore()) {
                        correctNum.getAndIncrement();
                    }
                });
            }
            // 获取考试列表对象
            ExamListVO vo = ExamListVO.builder()
                    .title(paper.getTitle())
                    .duration(paper.getDuration())
                    .score(sp.getTotalScore())
                    .startTime(paper.getStartTime())
                    .id(sp.getPaperId())
                    .courseName(finalCourseName)
                    .status(finalStatus)
                    .questionNum(questions.size())
                    .totalScore(paper.getTotalScore())
                    .correctNum(correctNum.get())
                    .answerNum(answers.size())
                    .build();
            examListVOS.add(vo);
        });
        return Map.of(
                "total", examListVOS.size(),
                "list", examListVOS.subList((page - 1) * size, Math.min(page * size, examListVOS.size()))
        );
    }

    @Override
    public void reservationExam(String id, long userId) {
        StudentPapers sp = new StudentPapers();
        sp.setPaperId(id);
        sp.setStudentId(userId);
        spsService.saveOrUpdate(sp);
    }

    @Override
    public List<ExamListVO> getReservationExamList(long userId, Long courseId) {
        List<TestPapers> papers = testPapersRepository.getTestPapersByCourseId(courseId);
        List<TestPapers> finalPapers = new ArrayList<>();
        // 获取不在StudentPapers中存在的试卷
        papers.forEach(paper -> {
            StudentPapers sp = spsService.getByUserIdAndPaperId(userId, paper.getId());
            if (sp == null) {
                finalPapers.add(paper);
            }
        });
        return BeanUtil.copyToList(finalPapers, ExamListVO.class);
    }

    @Override
    public List<ExamAnalysisVO> getAnalysisInfo(long userId, Long courseId) {
        List<StudentPapers> sps = spsService.getCompletedExam(userId);
        List<ExamAnalysisVO> examAnalysisVOS = new ArrayList<>();
        sps.forEach(sp -> {
            TestPapers paper = _getTestPapersById(sp.getPaperId(), userId);
            if (paper.getCourseId() != courseId) {
                return;
            }
            ExamAnalysisVO vo = ExamAnalysisVO.builder()
                    .id(sp.getPaperId())
                    .examName(paper.getTitle())
                    .courseId(paper.getCourseId())
                    .totalScore(paper.getTotalScore())
                    .score(sp.getTotalScore())
                    .build();
            examAnalysisVOS.add(vo);
        });
        return examAnalysisVOS;
    }

    @Override
    public List<EventTodoVO> getTodoExamList(long userId) {
        List<EventTodoVO> res = new ArrayList<>();
        spsService.getExamIdsByUserId(userId).forEach(sp -> {
            TestPapers paper = _getTestPapersById(sp.getPaperId(), userId);
            EventTodoVO vo = EventTodoVO.builder()
                    .id(sp.getPaperId())
                    .type("exam")
                    .title(paper.getTitle())
                    .endTime(paper.getStartTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime())
                    .courseId(paper.getCourseId())
                    .build();
            res.add(vo);
        });
        return res;
    }


    private TestPapers _getTestPapersById(String paperId, long userId) {
        log.info("内部获取试卷，试卷ID: {}, 用户ID: {}", paperId, userId);
        //比对用户id和试卷id是否有关联
        if (!spsService.exists(userId, paperId)) {
            log.error("获取试卷失败，不相关的试卷和学生，试卷ID: {}, 用户ID: {}", paperId, userId);
            throw new ExamException("不相关的试卷和学生");
        }
        TestPapers paper;
        try {
            paper = (TestPapers) redisTemplate.opsForValue().get("exam:paper:" + paperId);
            if ((paper != null)) {
                log.info("从缓存获取试卷成功，试卷ID: {}", paperId);
                return paper;
            }
        } catch (Exception e) {
            log.error("从缓存获取试卷异常，试卷ID: {}", paperId, e);
        }
        paper = testPapersRepository.findPaperById(paperId);
        if (paper == null) {
            log.error("获取试卷失败，试卷不存在，试卷ID: {}", paperId);
            throw new ExamException("试卷不存在");
        }
        try {
            redisTemplate.opsForValue().set("exam:paper:" + paperId, paper, 2, TimeUnit.HOURS);
            log.info("试卷存入缓存成功，试卷ID: {}", paperId);
        } catch (Exception e) {
            log.error("试卷存入缓存异常，试卷ID: {}", paperId, e);
        }
        return paper;
    }
}