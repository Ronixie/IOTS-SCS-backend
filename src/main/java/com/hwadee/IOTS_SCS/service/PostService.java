package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.entity.DTO.request.CreateDiscussionPostReq;
import com.hwadee.IOTS_SCS.entity.DTO.request.CreateReplyReq;
import com.hwadee.IOTS_SCS.entity.DTO.request.CreateSharingPostReq;
import com.hwadee.IOTS_SCS.entity.DTO.response.PostDetailDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.PostListDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.ReplyDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface PostService {

    // 创建帖子
    @Transactional
    PostDetailDTO createDiscussionPost(CreateDiscussionPostReq request);

    @Transactional
    PostDetailDTO createSharingPost(CreateSharingPostReq request);

    // 获取帖子详情
    PostDetailDTO getPostDetail(Long postId);

    //点赞
    @Transactional
    void likePost(Long postId);

    // 创建回复
    @Transactional
    ReplyDTO createReply(CreateReplyReq request);

    // 获取帖子列表
    List<PostListDTO> getDiscussionPostList(Long courseId);

    List<PostListDTO> getSharingPostList(Long courseId);

    // 删除帖子
    @Transactional
    void deletePost(Long postId);

    @Transactional
    void deleteReply(Long replyId);
}
