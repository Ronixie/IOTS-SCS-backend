package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

import java.time.LocalDateTime;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: FileInfo
* @Package: com.hwadee.IOTS_SCS.entity.POJO
* @Description: 数据库连接的文件信息类
* @author qiershi
* @date 2025/7/3 11:44
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class FileInfo {
    private String fileId;
    private String fileUrl;
    private String fileName;
    private String fileSize;
    private String fileUsage;
    private String status;
    private Long uploaderId;
    private LocalDateTime uploadedAt;
}