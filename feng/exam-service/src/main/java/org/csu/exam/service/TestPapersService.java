package org.csu.exam.service;

import org.csu.exam.entity.po.Answer;
import org.csu.exam.entity.po.TestPapers;
import org.csu.exam.entity.vo.EventTodoVO;
import org.csu.exam.entity.vo.ExamAnalysisVO;
import org.csu.exam.entity.vo.ExamListVO;
import org.csu.exam.entity.vo.ExamVO;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * 试卷服务接口
 */
public interface TestPapersService {
    /**
     * 根据ID获取试卷
     * @param id 试卷ID
     * @param userId 用户ID
     * @return 试卷对象
     */
    ExamVO getTestPapersById(String id, long userId);

    /**
     * 判断题目是否存在
     * @param paperId 试卷ID
     * @param questionId 题目ID
     * @return 是否存在
     */
    boolean existQuestion(String paperId,String questionId);

    /**
     * 提交试卷
     * @param id 试卷ID
     * @param userId 用户ID
     */
    boolean submitPaper(String id, long userId);

    /**
     * 获取学生对于试卷的回答
     * @param paperId 试卷ID
     * @param userId 用户ID
     * @return  学生对于试卷的回答
     */
    List<Answer> getPaperAnswersFromStudent(String paperId, long userId);

    /**
     * 保存或更新学生对于试卷的回答
     * @param answer 学生对于试卷的回答
     * @param userId 用户ID
     * @return 是否保存成功
     */
    boolean saveOrUpdateQuestionAnswer(Answer answer, long userId);

    /**
     * 更新学生试卷的分数
     * @param userId
     * @param paperId
     * @param totalScore
     * @param answers
     * @return
     */
    boolean updateScore(long userId, String paperId, double totalScore,List<Answer>answers);

    /**
     * 获取AI对于题目的解析
     * @param id 试卷ID
     * @param questionId questionId
     * @param userId 用户id
     * @return
     */
    Flux<String> getAIAnalysis(String id, String questionId, long userId);

    /**
     * 获取试卷的题目
     * @param id
     * @param questionId
     * @param userId
     * @return
     */
    TestPapers.Question getQuestionById(String id, String questionId, long userId);

    /**
     * 获取考试列表
     * @param userId
     * @return
     */
    Map<String,Object> getExamList(long userId, int page, int size,String courseName,String status,String title,Long courseId);

    void reservationExam(String id, long userId);

    List<ExamListVO> getReservationExamList(long userId, Long courseId);

    List<ExamAnalysisVO> getAnalysisInfo(long userId,Long courseId);

    List<EventTodoVO> getTodoExamList(long userId);
}