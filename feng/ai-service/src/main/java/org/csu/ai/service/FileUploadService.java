package org.csu.ai.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {
    /**
     * 上传文件
     * @param file 文件
     * @param userId 用户id
     * @param chatId 会话id
     * @return 返回文件路径
     */
    String uploadFile(MultipartFile file,long userId,String chatId) throws IOException;
}