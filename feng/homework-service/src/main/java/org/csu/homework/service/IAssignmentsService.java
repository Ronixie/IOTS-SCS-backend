package org.csu.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.homework.entity.dto.AssignmentDTO;
import org.csu.homework.entity.dto.SubmissionScoreDTO;
import org.csu.homework.entity.po.Assignments;
import org.csu.homework.entity.vo.AssignmentAnalysisVO;
import org.csu.homework.entity.vo.AssignmentDetailVO;
import org.csu.homework.entity.vo.EventTodoVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-07-02
 */
public interface IAssignmentsService extends IService<Assignments> {

    Map<String,Object> getAssignmentList(long userId, int page, int size, String courseName, String title, String status, String teacherName,String sort,Long courseId);

    AssignmentDetailVO getAssignmentById(long id, long userId);

    File downloadAttachment(long id, long userId, String tag,String attachment,long submissionId);

    void submitAssignment(long id, long userId, String content, MultipartFile[] files);

    Map<String, Object> getTeacherAssignmentList(long userId, int page, int size, String courseName, String title, Long courseId,String status);

    void updateAssignment(long id, long userId, AssignmentDTO dto, MultipartFile[] files);

    void createAssignment(long userId, Assignments assignments, MultipartFile[] files);

    void deleteAssignment(long id, long userId);

    Map<String, Object> getTeacherSubmissions(long id, long userId, int page, int size, String status, String studentKeyWord);

    void scoreSubmission(long id, long userId, SubmissionScoreDTO dto);

    void rejectSubmission(long id, long userId);

    List<AssignmentAnalysisVO> getAssignmentAnalysis(long userId,Long courseId);

    List<EventTodoVO> getTodoList(long userId);
}
