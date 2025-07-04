package com.hwadee.IOTS_SCS.entity.DTO.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: UploadFileDTO
* @Package: com.hwadee.IOTS_SCS.entity.DTO.request
* @Description: 上传文件的接收类
* @author qiershi
* @date 2025/7/2 14:18
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class UploadFileDTO {
    private MultipartFile file;
    private String fileUsage;
    private String token;
}