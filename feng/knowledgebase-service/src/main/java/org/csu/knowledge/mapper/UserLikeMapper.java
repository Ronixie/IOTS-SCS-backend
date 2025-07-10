package org.csu.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.csu.knowledge.entity.po.UserLike;

@Mapper
public interface UserLikeMapper extends BaseMapper<UserLike> {
    /**
     * 根据知识点ID获取点赞数量
     */
    int countByKpId(@Param("kpId") Long kpId);

    /**
     * 检查用户是否已点赞
     */
    UserLike selectByKpIdAndUserId(@Param("kpId") Long kpId, @Param("userId") Long userId);
} 