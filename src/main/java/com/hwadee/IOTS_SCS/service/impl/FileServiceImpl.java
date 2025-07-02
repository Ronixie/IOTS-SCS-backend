package com.hwadee.IOTS_SCS.service.impl;


import com.hwadee.IOTS_SCS.entity.DTO.response.FileDTO;
import com.hwadee.IOTS_SCS.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    public FileDTO upload(MultipartFile file, String fileType, int entityId, HttpServletRequest request) {
        String basePath = System.getProperty("user.dir") + "/res/file/";
        System.out.println(basePath);

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
                "/files/" + savedName;

        return new FileDTO(fileId, fileUrl, originalFilename, file.getSize());
    }
}