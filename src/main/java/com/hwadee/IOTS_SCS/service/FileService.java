package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.entity.DTO.response.FileDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileDTO upload(MultipartFile file, String fileType, int entityId, HttpServletRequest request);
}
