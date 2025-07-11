package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwadee.IOTS_SCS.entity.POJO.Post;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

import java.util.List;

public interface PostMapper{
    @Insert("INSERT INTO post (course_id, title, content, user_id, create_time, like_count) " +
            "VALUES (#{courseId}, #{title}, #{content}, #{userId}, #{createTime}, #{likeCount})")
    @Options(useGeneratedKeys = true, keyProperty = "postId")
    int insert(Post post);

    Post findById(Long postId);

    List<Post> findByCourseId(Long courseId);

    void delete(Long postId);

    void AddLikeCount(Post post);
}
