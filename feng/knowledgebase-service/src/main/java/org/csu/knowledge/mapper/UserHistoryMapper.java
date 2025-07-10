package org.csu.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.csu.knowledge.entity.po.UserHistory;

import java.util.List;

@Mapper
public interface UserHistoryMapper extends BaseMapper<UserHistory> {
    /**
     * 根据用户ID获取浏览历史
     */
    List<UserHistory> selectByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 根据用户ID和知识点ID获取历史记录
     */
    UserHistory selectByUserIdAndKpId(@Param("userId") Long userId, @Param("kpId") Long kpId);

    /**
     * 根据用户ID获取历史记录数量
     */
    int countByUserId(@Param("userId") Long userId);

    int countByKpId(Long kpId);
}