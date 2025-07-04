package com.hwadee.IOTS_SCS.entity.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: LogDTO
* @Package: com.hwadee.IOTS_SCS.entity.DTO.response
* @Description: 日志审计的传递类
* @author qiershi
* @date 2025/7/3 15:24
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class LogDTO {
    private Long userId;
    private String ip;
    private String uri;
    private LocalDateTime createdAt;
}