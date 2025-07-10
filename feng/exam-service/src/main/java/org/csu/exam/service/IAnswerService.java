package org.csu.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.exam.entity.po.Answer;

import java.util.List;

/**
 * <p>
 * 答案表 服务类
 * </p>
 *
 * @since 2025-06-27
 */
public interface IAnswerService extends IService<Answer> {
    /**
     * 保存或更新答案
     * @param answer 答案实体类
     * @param userId 用户id
     * @return 是否成功
     */
    boolean saveOrUpdate(Answer answer,long userId);


    /**
     * 获取学生的答案
     * @param paperId 试卷id
     * @param questionId 题目id
     * @param userId 用户id
     * @return
     */
    Answer getAnswerById(String paperId,String questionId,long userId);

    /**
     * 获取试卷上学生的所有答案
     * @param paperId
     * @param userId
     * @return
     */
    List<Answer> getAnswersByPaperAndUser(String paperId, long userId);

    /**
     * 获取指定试卷和用户的所有答案
     * @param paperId 试卷ID
     * @param userId 用户ID
     * @return 答案列表
     */
    List<Answer> getAnswersByPaperIdAndUserId(String paperId, long userId);

    /**
     * 批量保存答案
     * @param answers
     * @return
     */
    boolean saveAnswers(List<Answer> answers);
}
