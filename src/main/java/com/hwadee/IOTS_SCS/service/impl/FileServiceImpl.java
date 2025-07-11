package com.hwadee.IOTS_SCS.service.impl;


import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.FileInfoDTO;
import com.hwadee.IOTS_SCS.entity.POJO.FileInfo;
import com.hwadee.IOTS_SCS.mapper.FileMapper;
import com.hwadee.IOTS_SCS.mapper.UserMapper;
import com.hwadee.IOTS_SCS.service.FileService;
import com.hwadee.IOTS_SCS.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author qiershi
 * @version V1.0
 * Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
 * @ProjectName:smart_study
 * @Title: FileServiceImpl
 * @Package com.csu.smartstudy.service.impl
 * @Description: 文件上传下载的实现
 * @date 2025/6/30 9:42
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    FileMapper fileMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;

    public FileInfoDTO upload(MultipartFile file, String fileUsage, String token, HttpServletRequest request) {
        String basePath = System.getProperty("user.dir") + "/res/file/";

        if(file.isEmpty()) throw new IllegalArgumentException("上传文件不能为空");
        String originalFilename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String fileId = UUID.randomUUID().toString();
        String savedName = fileId + (extension != null ? "." + extension : "");

        File dest = new File(basePath + savedName);
        dest.getParentFile().mkdirs();

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败: " + e.getMessage(), e);
        }

        String fileUrl = request.getScheme() + "://" +
                request.getServerName() + ":" +
                request.getServerPort() +
                "/files?file_id=" + fileId;

        System.out.println(fileId);
        FileInfo info = new FileInfo();
        info.setFileId(fileId);
        info.setFileUrl(fileUrl);
        info.setFileName(originalFilename);
        info.setFileUsage(extension + ", " + fileUsage);
        info.setFileSize(String.valueOf(file.getSize()));
        info.setStatus("in_use");
        info.setUploaderId(Long.parseLong(jwtUtil.getUidFromToken(token.substring(7))));
        info.setUploadedAt(LocalDateTime.now());
        fileMapper.insertFileInfo(info);

        return new FileInfoDTO(fileId, fileUrl, originalFilename, file.getSize());
    }

    @Override
    public FileInfoDTO uploadAvatar(MultipartFile file, String token, HttpServletRequest request) {
        // 1. 获取用户ID (从token中获取)
        // 确保 token 字符串的长度足够，避免 substring 错误
        String userId = null;
        if (StringUtils.hasText(token) && token.length() >= 7) {
            userId = jwtUtil.getUidFromToken(token.substring(7));
        }
        if (userId == null) {
            throw new IllegalArgumentException("无法从 token 获取用户ID，或者 token 无效。");
        }
        Long uid = Long.parseLong(userId); // 将用户ID转换为Long类型

        // 2. 定义文件保存目录
        String baseDir = System.getProperty("user.dir") + File.separator + "res" + File.separator + "file" + File.separator;
        File uploadDir = new File(baseDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // 确保目录存在
        }

        if(file.isEmpty()) throw new IllegalArgumentException("上传文件不能为空");
        String originalFilename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename); // 使用 Spring 的 StringUtils 获取扩展名
        if (extension == null || !extension.matches("^(png|jpg|jpeg|gif|webp)$")) { // 限制图片类型
            throw new IllegalArgumentException("不支持的文件类型，请上传图片。");
        }

        // 3. 构建文件名：直接使用用户ID作为文件名，并添加扩展名。这样可以保证每个用户只有一个头像，每次上传覆盖旧的。
        String savedFileName = uid + "_avatar." + extension; // 例如：5_avatar.png
        File dest = new File(uploadDir, savedFileName); //


        try {
            file.transferTo(dest); //
            System.out.println("文件保存成功: " + dest.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("文件保存失败: " + e.getMessage());
            throw new RuntimeException("文件保存失败: " + e.getMessage(), e);
        }

        // 4. 更新 users 表中的 avatar_url
        // 存储在 users 表中的应该是文件名，例如 "5_avatar.png"
        try {
            userMapper.updateUserAvatar(uid, "api/api/users" + uid + "/avatar"); // !!! 关键：更新 users 表的头像URL !!!
            System.out.println("用户 " + uid + " 的头像URL已更新到数据库: " + savedFileName);
        } catch (Exception e) {
            System.err.println("更新用户头像URL失败: " + e.getMessage());
            // 如果更新用户头像失败，是否需要回滚文件上传？这里暂时不回滚，仅记录错误。
        }

        // 5. 记录文件信息到 files 表 (如果需要，建议保留，作为文件上传历史记录)
        FileInfo info = new FileInfo();
        info.setFileId(UUID.randomUUID().toString()); // 为 files 表生成一个唯一的 fileId
        // fileUrl 在这里可以设置为前端用于 GET 请求的完整 URL，或者只是保存内部识别码
        // 建议保存内部识别码或服务器相对路径，前端最终通过 /api/users/{uid}/avatar 来获取
        // 这里为了兼容性，可以继续保留原来的 URL 格式，但前端不会直接用这个来显示头像。
        info.setFileUrl(request.getScheme() + "://" + //
                request.getServerName() + ":" + //
                request.getServerPort() + //
                "/files?file_id=" + info.getFileId()); // // 这个URL需要有对应的Controller处理 /files 才能访问

        info.setFileName(originalFilename); //
        info.setFileUsage(extension + ",用户头像"); //
        info.setFileSize(String.valueOf(file.getSize())); //
        info.setStatus("in_use"); //
        info.setUploaderId(uid); //
        info.setUploadedAt(LocalDateTime.now()); //

        try {
            fileMapper.insertFileInfo(info); //
            System.out.println("FileInfo 插入成功到 files 表: " + info.getFileId());
        } catch (Exception e) {
            System.err.println("插入 FileInfo 到 files 表失败: " + e.getMessage());
            // 记录日志，但通常不抛出异常，因为头像已处理且用户头像URL已更新
        }

        // 6. 返回给前端的 FileInfoDTO
        // 返回给前端的 fileUrl 应该是 GET /api/users/{uid}/avatar 这样的路径，因为这是前端会用来显示头像的URL。
        // 而 FileInfo 中的 fileUrl 可以是内部的文件访问路径或标识。
        return new FileInfoDTO(info.getFileId(), "/api/users/" + userId + "/avatar", originalFilename, file.getSize()); //

    }

    @Override
    public boolean isDownloadAllowed(String fileId) {
        String downloadAllowed = fileMapper.isDownloadAllowed(fileId);
        return (downloadAllowed == null) || "true".equals(downloadAllowed);
    }
}