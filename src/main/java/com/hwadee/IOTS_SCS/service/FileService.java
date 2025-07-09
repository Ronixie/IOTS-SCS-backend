package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.entity.DTO.request.UploadFileDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.FileInfoDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface FileService {
    FileInfoDTO upload(UploadFileDTO fileDTO, HttpServletRequest request);
    boolean isDownloadAllowed(String fileId);
}
