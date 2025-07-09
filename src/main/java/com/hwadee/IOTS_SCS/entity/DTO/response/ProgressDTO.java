package com.hwadee.IOTS_SCS.entity.DTO.response;

import lombok.Data;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: ProgressDTO
* @Package: com.hwadee.IOTS_SCS.entity.DTO.response
* @Description: 返回进度信息
* @author qiershi
* @date 2025/7/9 9:36
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class ProgressDTO {
    private int completedLessons;
    private int progress;
    private int lastAccessedLessonId;
}