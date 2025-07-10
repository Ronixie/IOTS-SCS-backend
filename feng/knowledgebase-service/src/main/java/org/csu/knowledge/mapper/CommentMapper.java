package org.csu.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.csu.knowledge.entity.po.Comment;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 根据知识点ID获取评论列表
     */
    List<Comment> selectByKpId(@Param("kpId") Long kpId);

    /**
     * 根据知识点ID获取评论数量
     */
    int countByKpId(@Param("kpId") Long kpId);
} 