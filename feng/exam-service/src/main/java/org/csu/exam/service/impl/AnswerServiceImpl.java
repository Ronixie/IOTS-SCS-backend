package org.csu.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.csu.exam.entity.po.Answer;
import org.csu.exam.mapper.AnswerMapper;
import org.csu.exam.service.IAnswerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 答案表 服务实现类
 * </p>
 *
 * @since 2025-06-27
 */
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements IAnswerService {
    /**
     * 保存或更新答案
     * @param answer 答案实体
     * @param userId 用户ID
     * @return 操作是否成功
     * @throws IllegalArgumentException 如果试卷ID或问题ID不存在，或用户ID与试卷ID无关联
     */
    public boolean saveOrUpdate(Answer answer,long userId) {
        // 补全实体属性
        answer.setUserId(userId);
        answer.setAnswerTime(LocalDateTime.now());
        // 判断是否已经存在该答案，存在则更新，不存在则插入
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getPaperId, answer.getPaperId());
        wrapper.eq(Answer::getQuestionId, answer.getQuestionId());
        wrapper.eq(Answer::getUserId, answer.getUserId());
        Answer a = getOne(wrapper);
        if (a == null) {
            return save(answer);
        } else {
            return update(answer,wrapper);
        }
    }

    /**
     * 根据问题ID和用户ID获取答案
     * @param questionId
     * @param userId
     * @return
     */
    @Override
    public Answer getAnswerById(String paperId,String questionId, long userId) {
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getPaperId, paperId)
                .eq(Answer::getQuestionId, questionId)
                .eq(Answer::getUserId, userId);
        return getOne(wrapper);
    }

    /**
     * 根据试卷ID和用户ID获取该试卷上所有答案
     * @param paperId 试卷ID
     * @param userId 用户ID
     * @return
     */
    @Override
    public List<Answer> getAnswersByPaperAndUser(String paperId, long userId) {
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getPaperId, paperId)
                .eq(Answer::getUserId, userId);
        return list(wrapper);
    }

    /**
     * 获取指定试卷和用户的所有答案
     * @param paperId 试卷ID
     * @param userId 用户ID
     * @return 答案列表
     */
    @Override
    public List<Answer> getAnswersByPaperIdAndUserId(String paperId, long userId) {
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getPaperId, paperId)
                .eq(Answer::getUserId, userId);
        return list(wrapper);
    }

    /**
     * 保存多个答案
     * @param answers
     * @return
     */
    @Override
    @Transactional
    public boolean saveAnswers(List<Answer> answers) {
        for(Answer answer:answers){
            LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Answer::getPaperId, answer.getPaperId());
            wrapper.eq(Answer::getQuestionId, answer.getQuestionId());
            wrapper.eq(Answer::getUserId, answer.getUserId());
            Answer a = getOne(wrapper);
            if (a == null) {
                if(!save(answer)){
                    return false;
                }
            } else {
                if(!update(answer,wrapper)){
                    return false;
                }
            }
        }
        return true;
    }
}
