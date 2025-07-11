package com.hwadee.IOTS_SCS.service.impl;

import com.hwadee.IOTS_SCS.entity.DTO.request.CreateDiscussionPostReq;
import com.hwadee.IOTS_SCS.entity.DTO.request.CreateReplyReq;
import com.hwadee.IOTS_SCS.entity.DTO.request.CreateSharingPostReq;
import com.hwadee.IOTS_SCS.entity.DTO.response.PostDetailDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.PostListDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.ReplyDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Post;
import com.hwadee.IOTS_SCS.entity.POJO.Reply;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import com.hwadee.IOTS_SCS.mapper.CourseMapper;
import com.hwadee.IOTS_SCS.mapper.PostMapper;
import com.hwadee.IOTS_SCS.mapper.ReplyMapper;
import com.hwadee.IOTS_SCS.mapper.UserMapper;
import com.hwadee.IOTS_SCS.entity.POJO.FileInfo;
import com.hwadee.IOTS_SCS.entity.POJO.Post;
import com.hwadee.IOTS_SCS.entity.POJO.Reply;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import com.hwadee.IOTS_SCS.mapper.*;
import com.hwadee.IOTS_SCS.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ReplyMapper replyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private FileMapper fileMapper;

    // 创建帖子
    @Transactional
    @Override
    public PostDetailDTO createDiscussionPost(CreateDiscussionPostReq request) {
        // 创建帖子
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUserId(request.getUserId());
        post.setCourseId(request.getCourseId());
        post.setCreateTime(new Date());
        post.setLikeCount(0L);
        postMapper.insert(post);

        // 处理文件上传

/*        List<String> reqFileIds = request.getFileIds();
        List<String> postFileIds = new ArrayList<>();
        for(String fileId : reqFileIds) {
            postFileIds.add(fileId);
        }
        post.setFileIds(postFileIds);*/

        return getPostDetail(post.getPostId());
    }

    @Transactional
    @Override
    public PostDetailDTO createSharingPost(CreateSharingPostReq request) {
        // 创建帖子
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUserId(request.getUserId());
        post.setCreateTime(new Date());
        postMapper.insert(post);

        // 文件上传


        List<String> reqFileIds = request.getFileIds();
        List<String> postFileIds = new ArrayList<>();
        for(String fileId : reqFileIds) {
            postFileIds.add(fileId);
        }
        post.setFileIds(postFileIds);

        return getPostDetail(post.getPostId());
    }

    // 获取帖子详情
    @Override
    public PostDetailDTO getPostDetail(Long postId) {
        // 1. 获取帖子
        Post post = postMapper.findById(postId);
        if (post == null) {
            return null;
        }

        // 2. 获取关联文件
        List<String> fileIds = post.getFileIds();
        List<FileInfo> files = new ArrayList<>();
        for(String fileId : fileIds) {
            FileInfo fileInfo = fileMapper.getFileInfo(fileId);
            files.add(fileInfo);
        }

        // 3. 获取回复
        List<Reply> replies = replyMapper.findByPostId(postId);

        //转换为DTO
        PostDetailDTO dto = new PostDetailDTO();
        dto.setPostId(postId);
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreateTime(post.getCreateTime());
        dto.setFiles(files);

        //发帖人信息
        User author = userMapper.getUidUser(String.valueOf(post.getUserId()));
        dto.setUserId(post.getUserId());
        dto.setUserName(author.getName());
        dto.setAvatar(author.getAvatarUrl());
        dto.setLikeCount(post.getLikeCount());
        // 课程信息
        if (post.getCourseId() != null) {
            dto.setCourseId(post.getCourseId());
            dto.setCourseName(courseMapper.getCourseName(String.valueOf( post.getCourseId())));
        }

        // 回复列表
        dto.setReplies(replies.stream().map(reply -> {
            ReplyDTO rDto = new ReplyDTO();
            rDto.setReplyId(reply.getReplyId());
            rDto.setOfPostId(reply.getPostId());
            rDto.setContent(reply.getContent());
            rDto.setCreateTime(reply.getCreateTime());

            User replyUser = userMapper.getUidUser(String.valueOf(reply.getUserId()));
            rDto.setUserId(reply.getUserId());
            rDto.setUserName(replyUser.getName());
            rDto.setAvatar(replyUser.getAvatarUrl());

            return rDto;
        }).collect(Collectors.toList()));

        return dto;
    }

    //点赞
    @Transactional
    @Override
    public void likePost(Long postId) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            return;
        }
        post.setLikeCount(post.getLikeCount() + 1);
        postMapper.AddLikeCount(post);
    }


    // 创建回复
    @Transactional
    @Override
    public ReplyDTO createReply(CreateReplyReq request) {
        //创建回复
        Reply reply = new Reply();
        reply.setPostId(request.getPostId());
        reply.setUserId(request.getUserId());
        reply.setContent(request.getContent());
        reply.setCreateTime(new Date());
        replyMapper.insert(reply);

        //回复DTO
        ReplyDTO dto = new ReplyDTO();
        dto.setReplyId(reply.getReplyId());
        dto.setOfPostId(reply.getPostId());
        dto.setUserId(reply.getUserId());
        dto.setContent(reply.getContent());
        dto.setCreateTime(reply.getCreateTime());

        //用户信息
        User userInfo = userMapper.getUidUser(String.valueOf(reply.getUserId()));
        dto.setUserName(userInfo.getName());
        dto.setAvatar(userInfo.getAvatarUrl());

        return dto;
    }

    // 获取帖子列表
    @Override
    public List<PostListDTO> getDiscussionPostList(Long courseId) {
        List<Post> posts = postMapper.findByCourseId(courseId);

        return posts.stream().map(post -> {
            PostListDTO dto = new PostListDTO();
            dto.setPostId(post.getPostId());
            dto.setTitle(post.getTitle());
            dto.setUserId(post.getUserId());
            dto.setCourseId(post.getCourseId());
            dto.setCreateTime(post.getCreateTime());
            dto.setReplyCount(Integer.toUnsignedLong(replyMapper.findByPostId(post.getPostId()).size()));
            dto.setLikeCount(post.getLikeCount());
            dto.setPreview(post.getContent().substring(0, Math.min(100, post.getContent().length()))+"...");
            User userInfo = userMapper.getUidUser(String.valueOf(post.getUserId()));
            dto.setUserName(userInfo.getName());
            dto.setAvatar(userInfo.getAvatarUrl());

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PostListDTO> getSharingPostList(Long courseId) {
        List<Post> posts = postMapper.findByCourseId(courseId);

        return posts.stream().map(post -> {
            PostListDTO dto = new PostListDTO();
            dto.setPostId(post.getPostId());
            dto.setTitle(post.getTitle());
            dto.setUserId(post.getUserId());
            dto.setCreateTime(post.getCreateTime());
            dto.setReplyCount(Integer.toUnsignedLong(replyMapper.findByPostId(post.getPostId()).size()));
            dto.setLikeCount(post.getLikeCount());

            User userInfo = userMapper.getUidUser(String.valueOf(post.getUserId()));
            dto.setUserName(userInfo.getName());
            dto.setAvatar(userInfo.getAvatarUrl());

            return dto;
        }).collect(Collectors.toList());
    }

    // 删除帖子
    @Transactional
    @Override
    public void deletePost(Long postId) {
        //删除回复
        replyMapper.deleteByPostId(postId);

        //删除帖子
        postMapper.delete(postId);
    }

    //删除回复
    @Transactional
    @Override
    public void deleteReply(Long replyId) {
        replyMapper.delete(replyId);
    }
}
