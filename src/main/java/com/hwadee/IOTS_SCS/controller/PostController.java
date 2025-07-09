package com.hwadee.IOTS_SCS.controller;


import com.hwadee.IOTS_SCS.entity.DTO.CreateReplyReq;
import com.hwadee.IOTS_SCS.entity.DTO.PostDetailDTO;
import com.hwadee.IOTS_SCS.entity.DTO.PostListDTO;
import com.hwadee.IOTS_SCS.entity.DTO.ReplyDTO;
import com.hwadee.IOTS_SCS.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostServiceImpl postService;

    // 创建帖子
    @PostMapping
    public ResponseEntity<PostDetailDTO> createPost(@RequestBody CreatePostReq request) {
        PostDetailDTO dto = postService.createPost(request);
        return ResponseEntity.ok(dto);
    }

    // 获取帖子详情
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailDTO> getPostDetail(@PathVariable Long postId) {
        PostDetailDTO dto = postService.getPostDetail(postId);
        return ResponseEntity.ok(dto);
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
    @GetMapping
    public ResponseEntity<List<PostListDTO>> getPosts(
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<PostListDTO> posts = postService.getPosts(courseId, page, size);
        return ResponseEntity.ok(posts);
    }
}

