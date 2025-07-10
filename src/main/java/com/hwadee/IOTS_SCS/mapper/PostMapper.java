package com.hwadee.IOTS_SCS.mapper;

import com.hwadee.IOTS_SCS.entity.POJO.Post;

import java.util.List;

public interface PostMapper {
    void insert(Post post);

    Post findById(Long postId);

    List<Post> findByCourseId(Long courseId);

    void delete(Long postId);

    void AddLikeCount(Post post);
}
