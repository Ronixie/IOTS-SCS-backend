package org.csu.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.csu.exam.entity.po.Answer;

/**
 * <p>
 * 答案表 Mapper 接口
 * </p>
 *
 * @since 2025-06-27
 */
@Mapper
public interface AnswerMapper extends BaseMapper<Answer> {

}
