package com.hwadee.IOTS_SCS.entity.DTO.response;

import lombok.Data;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: LessonResDTO
* @Package: com.hwadee.IOTS_SCS.entity.DTO.response
* @Description: 
* @author qiershi
* @date 2025/7/10 16:15
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class LessonResDTO {
    private Long lessonId;
    private String lessonTitle;
    private String resourceType;
    private String fileId;
    private String allowDownload;
}