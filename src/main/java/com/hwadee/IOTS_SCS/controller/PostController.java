package com.hwadee.IOTS_SCS.controller;


import com.hwadee.IOTS_SCS.entity.DTO.*;
import com.hwadee.IOTS_SCS.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostServiceImpl postService;

    // 创建帖子
    @PostMapping("/createDiscussionPost")
    public ResponseEntity<PostDetailDTO> createDiscussionPost(
            @RequestPart("request") CreateDiscussionPostReq request
    ) {
        PostDetailDTO dto = postService.createDiscussionPost(request);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PostDetailDTO> createSharingPost(@RequestBody CreateSharingPostReq request) {
        PostDetailDTO dto = postService.createSharingPost(request);
        return ResponseEntity.ok(dto);
    }

    // 获取帖子详情
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailDTO> getPostDetail(@PathVariable Long postId) {
        PostDetailDTO dto = postService.getPostDetail(postId);
        return ResponseEntity.ok(dto);
    }

    // 点赞
    @PutMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        postService.likePost(postId);
        return ResponseEntity.noContent().build();
    }

    // 创建回复
    @PostMapping("/{postId}/replies")
    public ResponseEntity<ReplyDTO> createReply(
            @PathVariable Long postId,
            @RequestBody CreateReplyReq request
    ) {
        request.setPostId(postId);
        ReplyDTO dto = postService.createReply(request);
        return ResponseEntity.ok(dto);
    }

    // 获取帖子列表
    @GetMapping("/discussions")
    public ResponseEntity<List<PostListDTO>> getDiscussionPostList(
            @RequestParam("courseId") Long courseId
    ) {
        List<PostListDTO> dtoList = postService.getDiscussionPostList(courseId);
        return ResponseEntity.ok(dtoList);
    }

    // 获取分享列表
    @GetMapping("/sharings")
    public ResponseEntity<List<PostListDTO>> getSharingPostList(
            @RequestParam("courseId") Long courseId
    ) {
        List<PostListDTO> dtoList = postService.getSharingPostList(courseId);
        return ResponseEntity.ok(dtoList);
    }

    // 删除帖子
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // 删除回复
    @DeleteMapping("/{postId}/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long postId,
            @PathVariable Long replyId
    ) {
        postService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }
}

