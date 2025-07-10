package org.csu.homework.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.csu.exception.AssignmentException;
import org.csu.homework.client.CoursesClient;
import org.csu.homework.entity.dto.AssignmentDTO;
import org.csu.homework.entity.dto.SubmissionScoreDTO;
import org.csu.homework.entity.po.AssignmentSubmissions;
import org.csu.homework.entity.po.Assignments;
import org.csu.homework.entity.vo.*;
import org.csu.homework.enums.AssignmentStatus;
import org.csu.homework.mapper.AssignmentsMapper;
import org.csu.homework.service.IAssignmentSubmissionsService;
import org.csu.homework.service.IAssignmentsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/*
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2025-07-02
 */
@Service
@RequiredArgsConstructor
public class  AssignmentsServiceImpl extends ServiceImpl<AssignmentsMapper, Assignments> implements IAssignmentsService {
    private final IAssignmentSubmissionsService assignmentSubmissionsService;
    private final CoursesClient coursesClient;
    //private final UsersClient usersClient;
    @Value("${file.store.path}")
    private String rootDir;

    @Override
    public Map<String, Object> getAssignmentList(long userId, int page, int size,
                                                 String courseName, String title,
                                                 String status, String teacherName, String sort, Long courseId) {
        LambdaQueryWrapper<Assignments> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(title != null && !title.isEmpty(), Assignments::getTitle, title)
                .orderBy(sort != null && sort.equals("endDate"), true, Assignments::getEndDate)
                .eq(courseId != null, Assignments::getCourseId, courseId);
        List<Assignments> assignments = list(wrapper);
        int total = assignments.size();
        assignments = assignments.subList((page - 1) * size, Math.min(page * size, total));

        List<AssignmentsVO> res = assignments.stream()
                .map(a -> {
                    AssignmentsVO vo = new AssignmentsVO();
                    BeanUtil.copyProperties(a, vo);
                    vo.setCourseName("");
                    vo.setTeacherName("");
                    List<AssignmentSubmissions> submissions = assignmentSubmissionsService
                            .getSubmissionList(a.getAssignmentId(), userId);
                    // 处理提交记录并设置状态、分数和时间
                    _processSubmissions(vo, submissions);
                    return vo;
                })
                .toList();
        if (status != null && !status.isEmpty()) {
            return Map.of("total", total, "list", res.stream().filter(a -> a.getStatus().equals(status)).toList());
        }
        return Map.of("total", total, "list", res);
    }

    @Override
    public AssignmentDetailVO getAssignmentById(long id, long userId) {
        Assignments ans = getById(id);
        AssignmentDetailVO vo = new AssignmentDetailVO();
        BeanUtil.copyProperties(ans, vo);
        // TODO 获取对应的课程信息和教师信息，这里暂时置空(没必要了)
        vo.setCourseName("");
        List<AssignmentSubmissions> submissions = assignmentSubmissionsService.getSubmissionList(id, userId);
        vo.setSubmissions(submissions);
        vo.setTeacher(new AssignmentDetailVO.Teacher());
        if (submissions.isEmpty()) {
            vo.setStatus(AssignmentStatus.UNFINISHED.getValue());
        } else {
            // 使用与_processSubmissions方法相同的逻辑处理状态
            boolean allRejected = submissions.stream()
                    .allMatch(a -> a.getStatus().equals(AssignmentStatus.REJECTED.getValue()));
            boolean hasGraded = submissions.stream()
                    .anyMatch(a -> a.getStatus().equals(AssignmentStatus.GRADED.getValue()));

            if (allRejected) {
                vo.setStatus(AssignmentStatus.REJECTED.getValue());
            } else if (hasGraded) {
                vo.setStatus(AssignmentStatus.GRADED.getValue());
            } else {
                vo.setStatus(AssignmentStatus.SUBMITTED.getValue());
            }
        }
        return vo;
    }

    @Override
    public File downloadAttachment(long id, long userId, String tag, String attachment, long submissionId) {
        // TODO 判断userId是否与id,attachment对应的用户是否一致，不一致则返回错误（可选）
        if (Objects.equals(tag, "publish")) {
            Assignments assignments = getById(id);
            if (assignments == null || !Arrays.asList(JSON.parseObject(assignments.getAttachmentsJson(), String[].class)).contains(attachment)) {
                throw new AssignmentException("非法会话");
            }
        } else if (tag.equals("submission")) {
            AssignmentSubmissions submission = assignmentSubmissionsService.getById(submissionId);
            if (submission == null || submission.getAssignmentId() != id && !Arrays.asList(JSON.parseObject(submission.getAttachmentsJson(), String[].class)).contains(attachment)) {
                throw new AssignmentException("非法会话");
            }
        }
        return _downloadAttachment(attachment);
    }

    @Override
    public void submitAssignment(long id, long userId, String content, MultipartFile[] files) {
        // TODO 判断userId与id是否有关联（可选）

        // 判断改作业是否存在
        Assignments assignments = getById(id);
        if (assignments == null) {
            throw new AssignmentException("非法会话");
        }
        //判断是否已经截止，如果已经截止则返回错误
        if (LocalDateTime.now().isAfter(assignments.getEndDate())) {
            throw new AssignmentException("作业提交已截至");
        }
        // 获取提交过的次数
        long submissionCount = assignmentSubmissionsService.getSubmissionCount(id, userId);
        if (submissionCount > assignments.getMaxNumberSubmissions()) {
            throw new AssignmentException("提交次数不足");
        }
        // 保存文件
        String[] filePath = _saveFile(files);
        // 保存提交记录
        AssignmentSubmissions submission = new AssignmentSubmissions();
        submission.setSubmissionId(assignmentSubmissionsService.generateSubmissionId());
        submission.setAssignmentId(id);
        submission.setStudentId(userId);
        submission.setAnswerContent(content);
        submission.setAttachmentsJson(JSON.toJSONString(filePath));
        submission.setSubmissionNumber((int) submissionCount + 1);
        submission.setStatus(AssignmentStatus.SUBMITTED.getValue());
        assignmentSubmissionsService.save(submission);
    }

    @Override
    public Map<String, Object> getTeacherAssignmentList(long userId, int page, int size, String courseName, String title, Long courseId, String status) {
        // 获取作业
        LambdaQueryWrapper<Assignments> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(title != null && !title.isEmpty(), Assignments::getTitle, title);
        wrapper.eq(courseId != null, Assignments::getCourseId, courseId);
        wrapper.eq(Assignments::getTeacherId, userId);
        wrapper.ge(Objects.equals(status, "active"), Assignments::getEndDate, LocalDateTime.now());
        wrapper.le(Objects.equals(status, "completed"), Assignments::getEndDate, LocalDateTime.now());
        wrapper.orderByDesc(Assignments::getEndDate);
        List<Assignments> assignments = list(wrapper);
        int total = assignments.size();
        assignments = assignments.subList((page - 1) * size, Math.min(page * size, total));
        List<TeacherAssignmentsVO> res = BeanUtil.copyToList(assignments, TeacherAssignmentsVO.class);
        res.forEach(a -> {
            // TODO 获取课程名,暂时置空（没必要了）
            a.setCourseName("");
            a.setStatus(a.getEndDate().isAfter(LocalDateTime.now()) ? "active" : "completed");
        });
        return Map.of(
                "total", total, "list", res
        );
    }

    @Override
    public void updateAssignment(long id, long userId, AssignmentDTO dto, MultipartFile[] files) {
        Assignments assignments = getByIdAndUserId(id, userId);
        // 更新作业信息
        // 保存文件
        String[] originAttachments = JSON.parseObject(assignments.getAttachmentsJson(), String[].class);
        String[] paths = _saveFile(files);
        String[] attachments = JSON.parseObject(dto.getAttachmentsJson(), String[].class);
        // 要删除的附件
        String[] removedAttachment = _removeElements(originAttachments, attachments);
        attachments = Stream.concat(Arrays.stream(attachments), Arrays.stream(paths))
                .toArray(String[]::new);
        dto.setAttachmentsJson(JSON.toJSONString(attachments));
        BeanUtil.copyProperties(dto, assignments);
        updateById(assignments);
        // 删除旧附件
        _removeFile(removedAttachment);
    }

    private Assignments getByIdAndUserId(long id, long userId) {
        LambdaQueryWrapper<Assignments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Assignments::getAssignmentId, id)
                .eq(Assignments::getTeacherId, userId);
        Assignments assignments = getOne(wrapper);
        if (assignments == null) {
            throw new AssignmentException("非法会话");
        }
        return assignments;
    }

    @Override
    public void createAssignment(long userId, Assignments assignments, MultipartFile[] files) {
        // 保存文件
        String[] filePath = _saveFile(files);
        // 补全作业信息
        assignments.setAssignmentId(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000));
        assignments.setTeacherId(userId);
        assignments.setAttachmentsJson(JSON.toJSONString(filePath));
        save(assignments);
    }

    @Override
    public void deleteAssignment(long id, long userId) {
        // TODO 判断userId与id是否有关联（可选）
        Assignments assignments = getByIdAndUserId(id, userId);
        // 删除作业
        removeById(id);
        // 删除附件
        _removeFile(JSON.parseObject(assignments.getAttachmentsJson(), String[].class));
    }

    @Override
    public Map<String, Object> getTeacherSubmissions(long id, long userId, int page, int size, String status, String studentKeyWord) {
        Assignments assignments = getById(id);
        // 获取课程id
        Long courseId = assignments.getCourseId();
        // 获取总数
        //long total = assignmentSubmissionsService.count(new LambdaQueryWrapper<AssignmentSubmissions>().eq(AssignmentSubmissions::getAssignmentId, id));
        List<Long> studentIds = coursesClient.getStudentIdsByCourseId(courseId).getData();
        //List<Long> studentIds = List.of(123456L);
        int total = studentIds.size();
        List<SubmissionVO> res = new ArrayList<>();
        studentIds.forEach(s -> {
            // 获取学生信息
            UserVO vo = coursesClient.getUserInfo(s).getData();
            if (studentKeyWord == null || studentKeyWord.isEmpty() || s.toString().contains(studentKeyWord) || vo.getName().contains(studentKeyWord)) {
                List<AssignmentSubmissions> submissions = assignmentSubmissionsService.getSubmissionsByTeacher(id, s, status);
                List<SubmissionVO> vos = BeanUtil.copyToList(submissions, SubmissionVO.class);
                vos.forEach(s2 -> {
                    SubmissionVO.Student student = new SubmissionVO.Student();
                    student.setStudentName(vo.getName());
                    student.setStudentId(vo.getIdentity());
                    student.setStudentAvatar(vo.getAvatorUrl());
                    s2.setStudent(student);
                });
                res.addAll(vos);
            }
        });
        // 获取提交记录
        return Map.of(
                "total", total, "list", res
        );
    }

    @Override
    public void scoreSubmission(long id, long userId, SubmissionScoreDTO dto) {
        assignmentSubmissionsService.scoreSubmission(id, userId, dto);
    }

    @Override
    public void rejectSubmission(long id, long userId) {
        assignmentSubmissionsService.rejectSubmission(id, userId);
    }

    @Override
    public List<AssignmentAnalysisVO> getAssignmentAnalysis(long userId,Long courseId) {
        List<AssignmentAnalysisVO> res = new ArrayList<>();
        LambdaQueryWrapper<Assignments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Assignments::getCourseId, courseId);
        List<Assignments> assignments = list(wrapper);
        assignments.forEach(a -> {
            List<AssignmentSubmissions> submissions = assignmentSubmissionsService.getSubmissionList(a.getAssignmentId(), userId);
            AtomicReference<BigDecimal> maxScore = new AtomicReference<>(new BigDecimal(0));
            submissions.forEach(s -> {
                if (s.getScore() != null && s.getScore().compareTo(maxScore.get()) > 0) {
                    maxScore.set(s.getScore());
                }
            });
            AssignmentAnalysisVO vo = AssignmentAnalysisVO.builder()
                    .id(a.getAssignmentId())
                    .courseId(courseId)
                    .title(a.getTitle())
                    .score(maxScore.get()).build();
            res.add(vo);
        });
        return res;
    }

    @Override
    public List<EventTodoVO> getTodoList(long userId) {
        List<EventTodoVO> res = new ArrayList<>();
        coursesClient.getCourseIdsByUid(userId).getData().forEach(c -> {
            LambdaQueryWrapper<Assignments> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Assignments::getCourseId, c);
            List<Assignments> assignments = list(wrapper);
            assignments.forEach(a->{
                List<AssignmentSubmissions> submissions = assignmentSubmissionsService.getSubmissionList(a.getAssignmentId(), userId);
                if(submissions==null||submissions.isEmpty()){
                    EventTodoVO vo = EventTodoVO.builder()
                            .id(String.valueOf(a.getAssignmentId()))
                            .type("assignment")
                            .title(a.getTitle())
                            .endTime(a.getEndDate())
                            .courseId(c)
                            .build();
                    res.add(vo);
                }
            });
        });
        return res;
    }

    private String[] _removeElements(String[] source, String[] toRemove) {
        // 将待移除元素存入HashSet，以便快速查找
        Set<String> removeSet = new HashSet<>(Arrays.asList(toRemove));
        // 使用ArrayList动态存储结果
        List<String> resultList = new ArrayList<>();
        // 遍历源数组，仅添加不在removeSet中的元素
        for (String element : source) {
            if (!removeSet.contains(element)) {
                resultList.add(element);
            }
        }
        // 将结果转换为数组
        return resultList.toArray(new String[0]);
    }

    // 保存文件
    private String[] _saveFile(MultipartFile[] files) {
        if (files == null)
            return new String[0];
        int size = files.length;
        String[] filePath = new String[size];
        int index = 0;
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                // 获取文件名
                String fileName = file.getOriginalFilename();
                // 生成目录名
                String dirName = LocalDateTime.now().toString().replace(":", "-");
                // 确保目录存在
                File dir = new File(rootDir + File.separator + dirName);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (fileName == null) {
                    fileName = UUID.randomUUID().toString();
                }
                filePath[index++] = dirName + File.separator + fileName;
                // 保存文件
                file.transferTo(new File(dir, fileName));
            } catch (Exception e) {
                throw new AssignmentException(e);
            }
        }
        return filePath;
    }

    // 删除附件
    private void _removeFile(String[] paths) {
        for (String path : paths) {
            File file = new File(rootDir + File.separator + path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    // 下载附件
    private File _downloadAttachment(String attachment) {
        return new File(rootDir + File.separator + attachment);
    }

    // 处理提交记录
    private void _processSubmissions(AssignmentsVO vo, List<AssignmentSubmissions> submissions) {
        if (submissions == null || submissions.isEmpty()) {
            vo.setStatus(AssignmentStatus.UNFINISHED.getValue());
            return;
        }

        // 使用单次遍历计算最大分数、最终提交时间和拒绝状态
        SubmissionStats stats = submissions.stream()
                .reduce(
                        new SubmissionStats(),
                        (acc, submission) -> {
                            // 更新最大分数
                            if (submission.getScore() != null && submission.getScore().compareTo(acc.maxScore) > 0) {
                                acc.maxScore = submission.getScore();
                            }
                            // 更新最终提交时间
                            if (submission.getSubmittedAt() != null &&
                                    (acc.finalSubmissionTime == null || submission.getSubmittedAt().isAfter(acc.finalSubmissionTime))) {
                                acc.finalSubmissionTime = submission.getSubmittedAt();
                            }
                            // 更新拒绝状态（如果有任何一个不是REJECTED，则为false）
                            acc.allRejected = acc.allRejected && submission.getStatus().equals(AssignmentStatus.REJECTED.getValue());
                            return acc;
                        },
                        (acc1, acc2) -> {
                            // 合并两个累加器的结果（并行流时使用）
                            if (acc2.maxScore.compareTo(acc1.maxScore) > 0) {
                                acc1.maxScore = acc2.maxScore;
                            }
                            if (acc2.finalSubmissionTime != null &&
                                    (acc1.finalSubmissionTime == null || acc2.finalSubmissionTime.isAfter(acc1.finalSubmissionTime))) {
                                acc1.finalSubmissionTime = acc2.finalSubmissionTime;
                            }
                            acc1.allRejected = acc1.allRejected && acc2.allRejected;
                            return acc1;
                        }
                );

        // 设置分数和提交时间
        vo.setScore(stats.maxScore);
        vo.setSubmitTime(stats.finalSubmissionTime);

        // 根据统计结果设置状态
        if (stats.allRejected) {
            vo.setStatus(AssignmentStatus.REJECTED.getValue());
        } else if (stats.maxScore.compareTo(BigDecimal.ZERO) >= 0) {
            vo.setStatus(AssignmentStatus.GRADED.getValue());
        } else {
            vo.setStatus(AssignmentStatus.SUBMITTED.getValue());
        }
    }

    // 内部类用于收集统计信息
    private static class SubmissionStats {
        BigDecimal maxScore = new BigDecimal(-1);
        LocalDateTime finalSubmissionTime = null;
        boolean allRejected = true;
    }
}