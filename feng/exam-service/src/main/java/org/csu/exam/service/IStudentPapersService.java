package org.csu.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.exam.entity.po.StudentPapers;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 学生与试卷的关联表 服务类
 * </p>
 *
 * @author 
 * @since 2025-06-27
 */
public interface IStudentPapersService extends IService<StudentPapers> {

    /**
     * 判断学生和试卷是否相关
     * @param userId 学生id
     * @param paperId 试卷id
     * @return true or false
     */
    boolean exists(long userId, String paperId);

    /**
     * 更新学生试卷状态
     * @param userId 学生id
     * @param paperId 试卷id
     * @param status  状态
     * @return true or false
     */
    boolean updateStatus(long userId, String paperId, int status);

    /**
     * 获取学生提交试卷时间
     * @param userId 学生id
     * @param paperId 试卷id
     * @return true or false
     */
    LocalDateTime getSubmitTime(long userId, String paperId);

    /**
     * 更新学生试卷分数
     * @param userId
     * @param paperId
     * @param totalScore
     * @return
     */
    boolean updateScore(long userId, String paperId, double totalScore);

    /**
     * 获取学生试卷信息
     * @param userId
     * @param paperId
     * @return
     */
    StudentPapers getByUserIdAndPaperId(long userId, String paperId);

    /**
     * 获取学生考试id列表
     * @param userId
     * @return
     */
    List<StudentPapers> getExamIdsByUserId(long userId);

    List<StudentPapers> getCompletedExam(long userId);
}
