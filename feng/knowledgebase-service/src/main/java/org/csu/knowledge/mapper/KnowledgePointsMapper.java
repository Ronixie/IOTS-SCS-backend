package org.csu.knowledge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.csu.knowledge.entity.po.KnowledgePoints;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-07-05
 */
@Mapper
public interface KnowledgePointsMapper extends BaseMapper<KnowledgePoints> {

}
