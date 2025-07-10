package org.csu.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.csu.homework.entity.dto.SubmissionScoreDTO;
import org.csu.homework.entity.po.AssignmentSubmissions;
import org.csu.homework.enums.AssignmentStatus;
import org.csu.homework.mapper.AssignmentSubmissionsMapper;
import org.csu.homework.service.IAssignmentSubmissionsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2025-07-02
 */
@Service
public class AssignmentSubmissionsServiceImpl extends ServiceImpl<AssignmentSubmissionsMapper, AssignmentSubmissions> implements IAssignmentSubmissionsService {

    @Override
    public List<AssignmentSubmissions> getSubmissionList(long assignmentId, long userId) {
        LambdaQueryWrapper<AssignmentSubmissions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssignmentSubmissions::getAssignmentId, assignmentId)
                .eq(AssignmentSubmissions::getStudentId, userId);
        return list(wrapper);
    }

    @Override
    public long getSubmissionCount(long id, long userId) {
        LambdaQueryWrapper<AssignmentSubmissions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssignmentSubmissions::getAssignmentId, id)
                .eq(AssignmentSubmissions::getStudentId, userId);
        return count(wrapper);
    }

    @Override
    public long generateSubmissionId() {
        return System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000);
    }

    @Override
    public List<AssignmentSubmissions> getSubmissionsByTeacher(long id, long studentId, String status) {
        LambdaQueryWrapper<AssignmentSubmissions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssignmentSubmissions::getAssignmentId, id)
                .eq(AssignmentSubmissions::getStudentId, studentId)
                .eq(status!=null&&!status.isEmpty(),AssignmentSubmissions::getStatus, status)
                .orderByDesc(AssignmentSubmissions::getSubmittedAt);
        return list(wrapper);
    }

    @Override
    public void scoreSubmission(long id, long userId, SubmissionScoreDTO dto) {
        AssignmentSubmissions submission = getById(id);
        submission.setScore(dto.getScore());
        submission.setFeedback(dto.getFeedback());
        submission.setStatus(AssignmentStatus.GRADED.getValue());
        submission.setGradedAt(LocalDateTime.now());
        submission.setGradedById(userId);
        updateById(submission);
    }

    @Override
    public void rejectSubmission(long id, long userId) {
        AssignmentSubmissions submission = getById(id);
        submission.setStatus(AssignmentStatus.REJECTED.getValue());
        updateById(submission);
    }
}
