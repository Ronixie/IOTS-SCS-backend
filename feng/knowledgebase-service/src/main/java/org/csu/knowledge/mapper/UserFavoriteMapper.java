package org.csu.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.csu.knowledge.entity.po.UserFavorite;

import java.util.List;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
    /**
     * 根据知识点ID获取收藏数量
     */
    int countByKpId(@Param("kpId") Long kpId);

    /**
     * 检查用户是否已收藏
     */
    UserFavorite selectByKpIdAndUserId(@Param("kpId") Long kpId, @Param("userId") Long userId);

    /**
     * 根据用户ID获取收藏列表
     */
    List<UserFavorite> selectByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 根据用户ID获取收藏数量
     */
    int countByUserId(@Param("userId") Long userId);
} 