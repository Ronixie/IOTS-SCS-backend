package com.hwadee.IOTS_SCS.entity.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
* @ProjectName: smart_study
* @Title: File
* @Package: com.csu.smartstudy.entity
* @Description: 返回的文件信息
* @author qiershi
* @date 2025/6/30 17:00
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/

@Data
@AllArgsConstructor
public class FileInfoDTO {
    private String fileId;
    private String fileUrl;
    private String fileName;
    private long fileSize;
    private String fileUsage;
    private String status;
    private String uploaderId;
    private String uploadedAt;

    public FileInfoDTO(String fileId, String fileUrl, String fileName, long fileSize) {
        this.fileId = fileId;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
}