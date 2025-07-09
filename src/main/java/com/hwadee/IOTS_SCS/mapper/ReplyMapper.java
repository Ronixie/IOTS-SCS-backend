package com.hwadee.IOTS_SCS.mapper;

import com.hwadee.IOTS_SCS.entity.POJO.Reply;

import java.util.List;

public interface ReplyMapper {
    List<Reply> findByPostId(Long ofPostId);

    void insert(Reply reply);

    void deleteByPostId(Long postId);

    void delete(Long replyId);
}
