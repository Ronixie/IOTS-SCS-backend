package org.csu.exam.service;

import org.csu.exam.entity.po.TestPapers;
import org.csu.exam.entity.vo.ExamResultVO;
import org.csu.exam.entity.vo.ExamStatsVO;
import org.csu.exam.entity.vo.StudentExamDetailVO;

import java.util.List;
import java.util.Map;

/**
 * 教师端考试管理服务接口
 */
public interface TeacherExamService {
    

    /**
     * 获取课程下的考试列表
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param status 考试状态筛选
     * @param keyword 关键词搜索
     * @param page 页码
     * @param size 每页大小
     * @return 考试列表
     */
    Map<String, Object> getCourseExams(long teacherId, Long courseId, String status, String keyword, int page, int size);
    
    /**
     * 获取考试的学生成绩列表
     * @param teacherId 教师ID
     * @param examId 考试ID
     * @param keyword 关键词搜索
     * @param sortBy 排序方式
     * @param page 页码
     * @param size 每页大小
     * @return 学生成绩列表
     */
    Map<String, Object> getExamResults(long teacherId, String examId, String keyword, String sortBy, int page, int size);
    
    /**
     * 获取考试统计信息
     * @param teacherId 教师ID
     * @param examId 考试ID
     * @return 统计信息
     */
    ExamStatsVO getExamStats(long teacherId, String examId);
    
    /**
     * 获取学生考试详情
     * @param teacherId 教师ID
     * @param examId 考试ID
     * @param studentId 学生ID
     * @return 学生考试详情
     */
    StudentExamDetailVO getStudentExamDetail(long teacherId, String examId, Long studentId);

    /**
     * 教师手动重新评分
     * @param teacherId 教师ID
     * @param examId 考试ID
     * @param studentId 学生ID
     * @param requestBody 评分数据
     * @return 评分结果
     */
    ExamResultVO manualRescore(long teacherId, String examId, Long studentId, Map<String, Object> requestBody);
    
    /**
     * 确认更新分数
     * @param teacherId 教师ID
     * @param examId 考试ID
     * @param studentId 学生ID
     */
    void confirmScore(long teacherId, String examId, Long studentId);
    
    /**
     * 导出考试成绩
     * @param teacherId 教师ID
     * @param examId 考试ID
     * @return 下载链接
     */
    String exportExamResults(long teacherId, String examId);

    /**
     * 创建考试
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param requestBody 考试信息
     * @return 创建结果
     */
    String createExam(long teacherId, Long courseId, Map<String, Object> requestBody);

    /**
     * 编辑考试
     * @param teacherId 教师ID
     * @param examId 考试ID
     * @param requestBody 编辑内容
     * @return 编辑结果
     */
    String editExam(long teacherId, String examId, Map<String, Object> requestBody);

    /**
     * 删除考试
     * @param teacherId 教师ID
     * @param examId 考试ID
     */
    void deleteExam(long teacherId, String examId);

    void publishExam(long teacherId, String examId);

    List<TestPapers.Question> getExamQuestions(long teacherId, String examId);
}