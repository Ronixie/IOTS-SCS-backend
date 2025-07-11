package org.csu.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.homework.entity.dto.SubmissionScoreDTO;
import org.csu.homework.entity.po.AssignmentSubmissions;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-07-02
 */
public interface IAssignmentSubmissionsService extends IService<AssignmentSubmissions> {

    List<AssignmentSubmissions> getSubmissionList(long assignmentId, long userId);

    long getSubmissionCount(long id, long userId);

    long generateSubmissionId();

    List<AssignmentSubmissions> getSubmissionsByTeacher(long id, long studentIds, String status);

    void scoreSubmission(long id, long userId, SubmissionScoreDTO dto);

    void rejectSubmission(long id, long userId);
}
