package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.entity.DTO.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface PostService {
    PostDetailDTO createDiscussionPost(CreateDiscussionPostReq request);

    PostDetailDTO createSharingPost(CreateSharingPostReq request);

    // 获取帖子详情
    PostDetailDTO getPostDetail(Long postId);

    // 创建回复
    @Transactional
    ReplyDTO createReply(CreateReplyReq request);

    // 获取帖子列表
    List<PostListDTO> getDiscussionPostList(Long courseId);

    // 删除帖子
    @Transactional
    void deletePost(Long postId);

    @Transactional
    void deleteReply(Long replyId);
}
