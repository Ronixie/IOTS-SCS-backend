package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.entity.DTO.response.FileInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileInfoDTO upload(MultipartFile file, String fileUsage, String token, HttpServletRequest request);
    FileInfoDTO uploadAvatar(MultipartFile file, String token, HttpServletRequest request);
    boolean isDownloadAllowed(String fileId);
}
