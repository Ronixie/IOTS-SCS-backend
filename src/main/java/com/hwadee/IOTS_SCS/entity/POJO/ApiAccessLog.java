package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: Log
* @Package: com.hwadee.IOTS_SCS.entity.POJO
* @Description: 日志类
* @author qiershi
* @date 2025/7/2 10:20
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class ApiAccessLog {
    private Long logId;
    private Long userId;
    private String ip;
    private String uri;
    private String query;
    private Integer durationMs;
    private Integer statusCode;
    private String userAgent;
    private LocalDateTime createdAt;
}