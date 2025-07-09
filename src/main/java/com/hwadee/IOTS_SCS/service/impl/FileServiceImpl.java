package com.hwadee.IOTS_SCS.service.impl;


import com.hwadee.IOTS_SCS.entity.DTO.request.UploadFileDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.FileInfoDTO;
import com.hwadee.IOTS_SCS.entity.POJO.FileInfo;
import com.hwadee.IOTS_SCS.mapper.FileMapper;
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
    private JwtUtil jwtUtil;

    public FileInfoDTO upload(UploadFileDTO dto, HttpServletRequest request) {
        String basePath = System.getProperty("user.dir") + "/res/file/";
        MultipartFile file = dto.getFile();

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

        FileInfo info = new FileInfo();
        info.setFileId(fileId);
        info.setFileUrl(fileUrl);
        info.setFileName(originalFilename);
        info.setFileUsage("教学");
        info.setFileSize(String.valueOf(file.getSize()));
        info.setUploaderId(Long.parseLong(jwtUtil.getUidFromToken(dto.getToken().substring(7))));
        info.setUploadedAt(LocalDateTime.now());
        fileMapper.insertFileInfo(info);

        return new FileInfoDTO(fileId, fileUrl, originalFilename, file.getSize());
    }

    @Override
    public boolean isDownloadAllowed(String fileId) {
        String downloadAllowed = fileMapper.isDownloadAllowed(fileId);
        return (downloadAllowed == null) || "true".equals(downloadAllowed);
    }
}