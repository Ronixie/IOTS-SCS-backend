package org.csu.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.exam.clients.CoursesClient;
import org.csu.exam.entity.po.Answer;
import org.csu.exam.entity.po.StudentPapers;
import org.csu.exam.entity.po.TestPapers;
import org.csu.exam.entity.vo.ExamResultVO;
import org.csu.exam.entity.vo.ExamStatsVO;
import org.csu.exam.entity.vo.StudentExamDetailVO;
import org.csu.exam.entity.vo.UserVO;
import org.csu.exam.service.IAnswerService;
import org.csu.exam.service.IStudentPapersService;
import org.csu.exam.service.TeacherExamService;
import org.csu.exception.ExamException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.UUID;


/**
 * 教师端考试管理服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherExamServiceImpl implements TeacherExamService {

    private final MongoTemplate mongoTemplate;
    private final IStudentPapersService studentPapersService;
    private final IAnswerService answerService;
    private final CoursesClient coursesClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Map<String, Object> getCourseExams(long teacherId, Long courseId, String status, String keyword, int page, int size) {
        log.info("获取课程考试列表, teacherId: {}, courseId: {}, status: {}, keyword: {}",
                teacherId, courseId, status, keyword);

        // 从MongoDB获取试卷信息
        Query query = new Query(Criteria.where("courseId").is(courseId));
        if (StringUtils.hasText(keyword)) {
            query.addCriteria(Criteria.where("title").regex(keyword, "i"));
        }

        List<TestPapers> testPapers = mongoTemplate.find(query, TestPapers.class);

        // 获取每个考试的参与人数
        List<Map<String, Object>> examList = testPapers.stream().map(paper -> {
            String finalStatus;
            if (paper.getStartTime().getTime() > System.currentTimeMillis()) {
                finalStatus = "pending";
            } else if (paper.getStartTime().getTime() + (long) paper.getDuration() * 60 * 1000 <= System.currentTimeMillis()) {
                finalStatus = "completed";
            } else {
                finalStatus = "ongoing";
            }
            if (status != null && !status.equals(finalStatus)) {
                return null;
            }
            Map<String, Object> exam = new HashMap<>();
            exam.put("id", paper.getId());
            exam.put("title", paper.getTitle());
            exam.put("paperStatus", paper.getStatus());
            exam.put("startTime", paper.getStartTime());
            exam.put("duration", paper.getDuration());
            exam.put("totalScore", paper.getTotalScore());
            exam.put("questionNum", paper.getQuestions().size());
            exam.put("examStatus", finalStatus);
            // 获取参与人数
            LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StudentPapers::getPaperId, paper.getId());
            long participantCount = studentPapersService.count(wrapper);
            exam.put("participantCount", participantCount);

            // 如果是已完成的考试，计算统计数据
            if (finalStatus.equals("completed")) {
                List<StudentPapers> studentPapers = studentPapersService.list(wrapper);
                if (!studentPapers.isEmpty()) {
                    List<Double> scores = studentPapers.stream()
                            .map(StudentPapers::getTotalScore)
                            .toList();

                    exam.put("averageScore", scores.stream().mapToDouble(Double::doubleValue).average().orElse(0));
                    exam.put("highestScore", scores.stream().mapToDouble(Double::doubleValue).max().orElse(0));
                    exam.put("lowestScore", scores.stream().mapToDouble(Double::doubleValue).min().orElse(0));
                }
            }

            return exam;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        // 分页
        return _getPaginationFromExamList(page, size, examList);
    }

    @Override
    public Map<String, Object> getExamResults(long teacherId, String examId, String keyword, String sortBy, int page, int size) {
        log.info("获取考试学生成绩, teacherId: {}, examId: {}, keyword: {}, sortBy: {}",
                teacherId, examId, keyword, sortBy);

        // 获取试卷信息
        TestPapers testPaper = _getTestPapersById(examId, teacherId);

        // 获取所有参与考试的学生
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getPaperId, examId);
        List<StudentPapers> studentPapers = studentPapersService.list(wrapper);

        // 构建学生成绩列表
        List<Map<String, Object>> results = studentPapers.stream().map(sp -> {
            Map<String, Object> result = new HashMap<>();
            UserVO student = coursesClient.getUserInfo(sp.getStudentId()).getData();
            result.put("studentId", sp.getStudentId());
            result.put("studentName", student.getName());
            result.put("studentNumber", student.getIdentity());
            result.put("studentAvatar", student.getAvatorUrl());
            result.put("score", sp.getTotalScore());
            result.put("totalScore", testPaper.getTotalScore());
            result.put("submitTime", sp.getSubmitTime());
            result.put("status", sp.getStatus() == 1 ? "completed" : "ongoing");

            // 计算正确题数和正确率
            List<Answer> answers = answerService.getAnswersByPaperIdAndUserId(examId, sp.getStudentId());
            long correctCount = answers.stream()
                    .filter(answer -> answer.getScore() > 0)
                    .count();
            result.put("correctCount", correctCount);
            result.put("accuracy", !testPaper.getQuestions().isEmpty() ?
                    ((double) correctCount) / testPaper.getQuestions().size() * 100 : 0);

            return result;
        }).collect(Collectors.toList());

        // 搜索筛选
        if (StringUtils.hasText(keyword)) {
            results = results.stream()
                    .filter(result -> result.get("studentName").toString().toLowerCase().contains(keyword.toLowerCase()) ||
                            result.get("studentNumber").toString().contains(keyword))
                    .collect(Collectors.toList());
        }

        // 排序
        switch (sortBy) {
            case "score":
                results.sort((a, b) -> Integer.compare((Integer) b.get("score"), (Integer) a.get("score")));
                break;
            case "name":
                results.sort(Comparator.comparing(a -> a.get("studentName").toString()));
                break;
            case "submitTime":
                results.sort((a, b) -> ((LocalDateTime) b.get("submitTime")).compareTo((LocalDateTime) a.get("submitTime")));
                break;
        }

        // 分页
        return _getPaginationFromExamList(page, size, results);
    }

    @Override
    public ExamStatsVO getExamStats(long teacherId, String examId) {
        log.info("获取考试统计信息, teacherId: {}, examId: {}", teacherId, examId);

        // 获取试卷信息
        TestPapers testPaper = mongoTemplate.findById(examId, TestPapers.class);
        if (testPaper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 获取所有参与考试的学生
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getPaperId, examId);
        List<StudentPapers> studentPapers = studentPapersService.list(wrapper);


        int totalScore = testPaper.getTotalScore();

        ExamStatsVO stats = new ExamStatsVO();
        stats.setExamId(examId);
        stats.setExamTitle(testPaper.getTitle());
        stats.setTotalStudents(studentPapers.size());
        stats.setTotalScore(totalScore);
        stats.setQuestionNum(testPaper.getQuestions().size());

        if (!studentPapers.isEmpty()) {
            List<Double> scores = studentPapers.stream()
                    .map(StudentPapers::getTotalScore)
                    .toList();

            double averageScore = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double highestScore = scores.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double lowestScore = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0);

            stats.setAverageScore(Math.round(averageScore * 100.0) / 100.0);
            stats.setHighestScore(highestScore);
            stats.setLowestScore(lowestScore);

            // 计算各分数段比例
            int totalStudents = scores.size();
            long excellentCount = scores.stream().filter(s -> s >= totalScore * 0.9).count();
            long goodCount = scores.stream().filter(s -> s >= totalScore * 0.8 && s < totalScore * 0.9).count();
            long mediumCount = scores.stream().filter(s -> s >= totalScore * 0.7 && s < totalScore * 0.8).count();
            long passCount = scores.stream().filter(s -> s >= totalScore * 0.6 && s < totalScore * 0.7).count();
            long failCount = scores.stream().filter(s -> s < totalScore * 0.6).count();

            stats.setExcellentRate(((double) excellentCount) / totalStudents * 100);
            stats.setGoodRate(((double) goodCount) / totalStudents * 100);
            stats.setMediumRate(((double) mediumCount) / totalStudents * 100);
            stats.setPassRateDetail(((double) passCount) / totalStudents * 100);
            stats.setFailRate(((double) failCount) / totalStudents * 100);
            stats.setPassRate(((double) (totalStudents - failCount)) / totalStudents * 100);
        }

        return stats;
    }

    @Override
    public StudentExamDetailVO getStudentExamDetail(long teacherId, String examId, Long studentId) {
        log.info("获取学生考试详情, teacherId: {}, examId: {}, studentId: {}", teacherId, examId, studentId);

        // 获取试卷信息
        TestPapers testPaper = _getTestPapersById(examId, teacherId);

        // 获取学生试卷信息
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getPaperId, examId)
                .eq(StudentPapers::getStudentId, studentId);
        StudentPapers studentPaper = studentPapersService.getOne(wrapper);
        if (studentPaper == null) {
            throw new RuntimeException("学生试卷信息不存在");
        }

        // 获取学生答案
        List<Answer> answers = answerService.getAnswersByPaperIdAndUserId(examId, studentId);
        Map<String, Answer> answerMap = answers.stream()
                .collect(Collectors.toMap(Answer::getQuestionId, answer -> answer));

        // 构建题目详情
        List<StudentExamDetailVO.QuestionDetailVO> questionDetails = testPaper.getQuestions().stream()
                .map(question -> {
                    StudentExamDetailVO.QuestionDetailVO detail = new StudentExamDetailVO.QuestionDetailVO();
                    detail.setQuestionId(question.getQuestionId());
                    detail.setContent(question.getContent());
                    detail.setType(question.getType());
                    detail.setCorrectAnswer(question.getAnswer());
                    detail.setTotalScore(question.getScore());

                    Answer answer = answerMap.get(question.getQuestionId());
                    if (answer != null) {
                        detail.setStudentAnswer(answer.getAnswer());
                        detail.setScore(answer.getScore());
                    }

                    return detail;
                }).collect(Collectors.toList());

        // 构建返回结果
        StudentExamDetailVO result = new StudentExamDetailVO();
        UserVO user = coursesClient.getUserInfo(studentId).getData();
        result.setStudentId(studentPaper.getStudentId());
        result.setStudentNumber(user.getIdentity());
        result.setStudentName(user.getName());
        result.setExamId(examId);
        result.setExamTitle(testPaper.getTitle());
        result.setScore(studentPaper.getTotalScore());
        result.setTotalScore(testPaper.getTotalScore());
        result.setSubmitTime(studentPaper.getSubmitTime());
        result.setQuestions(questionDetails);

        return result;
    }


    @Override
    public ExamResultVO manualRescore(long teacherId, String examId, Long studentId, Map<String, Object> requestBody) {
        log.info("教师手动重新评分, teacherId: {}, examId: {}, studentId: {}", teacherId, examId, studentId);

        // 获取学生考试详情
        StudentExamDetailVO detail = getStudentExamDetail(teacherId, examId, studentId);

        // 解析评分数据
        List<Map<String, Object>> scoresData = (List<Map<String, Object>>) requestBody.get("scores");
        Map<String, Double> questionScores = new HashMap<>();
        Map<String, String> questionComments = new HashMap<>();

        for (Map<String, Object> scoreData : scoresData) {
            String questionId = (String) scoreData.get("questionId");
            double score = ((Number) scoreData.get("score")).doubleValue();
            String comment = (String) scoreData.get("comment");

            questionScores.put(questionId, score);
            if (comment != null && !comment.trim().isEmpty()) {
                questionComments.put(questionId, comment);
            }
            Answer answer = answerService.getAnswerById(examId, questionId, studentId);
            answer.setScore(score);
            answerService.saveAnswers(List.of(answer));
        }

        // 计算新总分
        double newTotalScore = questionScores.values().stream().mapToDouble(Double::doubleValue).sum();

        studentPapersService.updateScore(studentId, examId, newTotalScore);
        // 构建评分结果
        ExamResultVO result = new ExamResultVO();
        result.setStudentId(studentId);
        result.setStudentName(detail.getStudentName());
        result.setExamId(examId);
        result.setOriginalScore(detail.getScore());
        result.setNewScore(newTotalScore);
        result.setScoreChange(result.getNewScore() - result.getOriginalScore());
        result.setTotalScore(detail.getTotalScore());
        result.setSubmitTime(detail.getSubmitTime());
        result.setStatus("completed");

        // 构建题目评分结果
        List<ExamResultVO.QuestionResultVO> questionResults = new ArrayList<>();
        int correctCount = 0;

        for (int i = 0; i < detail.getQuestions().size(); i++) {
            StudentExamDetailVO.QuestionDetailVO question = detail.getQuestions().get(i);
            ExamResultVO.QuestionResultVO questionResult = new ExamResultVO.QuestionResultVO();
            questionResult.setQuestionId(question.getQuestionId());
            questionResult.setContent(question.getContent());
            questionResult.setType(question.getType());
            questionResult.setStudentAnswer(question.getStudentAnswer());
            questionResult.setCorrectAnswer(question.getCorrectAnswer());
            questionResult.setOriginalScore(question.getScore());

            // 尝试通过题目ID或索引获取新分数
            Double newScore = questionScores.get(question.getQuestionId());
            if (newScore == null) {
                // 如果通过题目ID找不到，尝试通过索引
                String indexKey = "question_" + i;
                newScore = questionScores.get(indexKey);
            }
            questionResult.setNewScore(newScore != null ? newScore.intValue() : question.getScore());
            questionResult.setTotalScore(question.getTotalScore());

            String comment = questionComments.get(question.getQuestionId());
            if (comment == null) {
                comment = questionComments.get("question_" + i);
            }
            questionResult.setAiComment(comment != null ? comment : "教师手动评分");

            if (questionResult.getNewScore() > 0) {
                correctCount++;
            }

            questionResults.add(questionResult);
        }

        result.setCorrectCount(correctCount);
        result.setAccuracy((double) correctCount / detail.getQuestions().size() * 100);
        result.setQuestions(questionResults);

        return result;
    }

    @Override
    public void confirmScore(long teacherId, String examId, Long studentId) {
        log.info("确认更新分数, teacherId: {}, examId: {}, studentId: {}", teacherId, examId, studentId);

        // 更新学生试卷的总分
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getPaperId, examId)
                .eq(StudentPapers::getStudentId, studentId);
        StudentPapers studentPaper = studentPapersService.getOne(wrapper);

        if (studentPaper != null) {
            // 重新计算总分
            List<Answer> answers = answerService.getAnswersByPaperIdAndUserId(examId, studentId);
            double newTotalScore = answers.stream()
                    .mapToDouble(Answer::getScore)
                    .sum();

            studentPaper.setTotalScore(newTotalScore);
            studentPapersService.updateById(studentPaper);

            log.info("学生 {} 的分数已更新为: {}", studentId, newTotalScore);
        } else {
            throw new RuntimeException("学生试卷信息不存在");
        }
    }

    @Override
    public String exportExamResults(long teacherId, String examId) {
        log.info("导出考试成绩, teacherId: {}, examId: {}", teacherId, examId);

        // 这里应该生成Excel文件并返回下载链接
        // 由于是模拟实现，这里返回一个模拟的下载链接
        return "/download/exam-results-" + examId + ".xlsx";
    }


    @Override
    public String createExam(long teacherId, Long courseId, Map<String, Object> requestBody) {
        // 构建试卷对象
        TestPapers paper = new TestPapers();
        paper.setTitle((String) requestBody.get("title"));
        paper.setCourseId(courseId);
        paper.setTeacherId(teacherId);
        paper.setStartTime(new Date((Long) requestBody.get("startTime")));
        paper.setDuration((Integer) requestBody.get("duration"));
        paper.setTotalScore((Integer) requestBody.get("totalScore"));
        paper.setCreateTime(new Date());
        paper.setStatus("未发布");
        // 处理题目
        _handleQuestions(requestBody, paper);
        return paper.getId();
    }


    @Override
    public String editExam(long teacherId, String examId, Map<String, Object> requestBody) {
        TestPapers paper = mongoTemplate.findById(examId, TestPapers.class);
        if (paper == null) throw new RuntimeException("试卷不存在");
        if (!Objects.equals(paper.getTeacherId(), teacherId)) throw new RuntimeException("无权限");
        if (requestBody.containsKey("title")) paper.setTitle((String) requestBody.get("title"));
        if (requestBody.containsKey("startTime"))
            paper.setStartTime(new Date((Long) requestBody.get("startTime")));
        if (requestBody.containsKey("duration")) paper.setDuration((Integer) requestBody.get("duration"));
        if (requestBody.containsKey("totalScore")) paper.setTotalScore((Integer) requestBody.get("totalScore"));
        // 处理题目
        _handleQuestions(requestBody, paper);
        return paper.getId();
    }

    @Override
    public void deleteExam(long teacherId, String examId) {
        TestPapers paper = mongoTemplate.findById(examId, TestPapers.class);
        if (paper == null) throw new RuntimeException("试卷不存在");
        if (!Objects.equals(paper.getTeacherId(), teacherId)) throw new RuntimeException("无权限");
        mongoTemplate.remove(paper);
    }

    @Override
    public void publishExam(long teacherId, String examId) {
        TestPapers paper = mongoTemplate.findById(examId, TestPapers.class);
        if (paper == null) throw new RuntimeException("试卷不存在");
        if (!Objects.equals(paper.getTeacherId(), teacherId)) throw new RuntimeException("无权限");
        paper.setStatus("已发布");
        mongoTemplate.save(paper);

        List<Long> ids = coursesClient.getStudentIdsByCourseId(paper.getCourseId()).getData();
        List<StudentPapers> sps = new ArrayList<>();
        for (Long studentId : ids) {
            StudentPapers sp = new StudentPapers();
            sp.setPaperId(examId);
            sp.setStudentId(studentId);
            sps.add(sp);
        }
        studentPapersService.saveBatch(sps);
    }

    @Override
    public List<TestPapers.Question> getExamQuestions(long teacherId, String examId) {
        TestPapers paper = mongoTemplate.findById(examId, TestPapers.class);
        if (paper == null) throw new RuntimeException("试卷不存在");
        if (!Objects.equals(paper.getTeacherId(), teacherId)) throw new RuntimeException("无权限");
        return paper.getQuestions();
    }

    private void _handleQuestions(Map<String, Object> requestBody, TestPapers paper) {
        List<Map<String, Object>> questionsRaw = (List<Map<String, Object>>) requestBody.get("questions");
        if (questionsRaw != null) {
            List<TestPapers.Question> questions = new ArrayList<>();
            List<Integer> orders = new ArrayList<>();
            for (int i = 0; i < questionsRaw.size(); i++) {
                Map<String, Object> q = questionsRaw.get(i);
                TestPapers.Question question = new TestPapers.Question();
                int order = q.get("order") != null ? Integer.parseInt(q.get("order").toString()) : i + 1;
                orders.add(order);

                question.setQuestionId(q.get("questionId") != null ? q.get("questionId").toString() : "Q00"+order);
                question.setType((String) q.get("type"));
                question.setContent((String) q.get("content"));
                List<String> options = (List<String>) q.get("options");
                List<TestPapers.Question.Option> ops = new ArrayList<>();
                for (int j = 0; j < options.size(); j++) {
                    TestPapers.Question.Option op = new TestPapers.Question.Option();
                    op.setOptionId(String.valueOf((char) ('A' + j)));
                    op.setContent(options.get(j));
                    ops.add(op);
                }
                question.setOptions(ops);
                question.setAnswer((String) q.get("answer"));
                question.setScore(Integer.parseInt(q.get("score").toString()));
                question.setAnalysis((String) q.get("analysis"));

                questions.add(question);
            }
            /*List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                indices.add(i);
            }
            // 按order排序索引
            indices.sort(Comparator.comparingInt(orders::get));

            // 根据排序后的索引重建questions列表
            List<TestPapers.Question> sortedQuestions = new ArrayList<>();
            for (int index : indices) {
                sortedQuestions.add(questions.get(index));
            }*/
            paper.setQuestions(questions);
        }
        // 保存到 MongoDB
        mongoTemplate.save(paper);

    }

    private TestPapers _getTestPapersById(String paperId, long userId) {
        log.info("获取试卷，试卷ID: {}, teacherID: {}", paperId, userId);
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
        paper = mongoTemplate.findById(paperId, TestPapers.class);
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

    private Map<String, Object> _getPaginationFromExamList(int page, int size, List<Map<String, Object>> examList) {
        int total = examList.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Map<String, Object>> pagedList = examList.subList(start, end);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pagedList);
        result.put("total", total);
        result.put("pages", (total + size - 1) / size);
        result.put("current", page);
        result.put("size", size);
        return result;
    }

} 