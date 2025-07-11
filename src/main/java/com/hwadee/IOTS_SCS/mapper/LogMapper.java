package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hwadee.IOTS_SCS.entity.DTO.response.LogDTO;
import com.hwadee.IOTS_SCS.entity.POJO.ApiAccessLog;

import java.time.LocalDateTime;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: LogMapper
* @Package: com.hwadee.IOTS_SCS.mapper
* @Description: 日志
* @author qiershi
* @date 2025/7/2 10:20
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
public interface LogMapper extends BaseMapper<ApiAccessLog> {
    int insert(ApiAccessLog log);
    IPage<LogDTO> getUidLogs(IPage<LogDTO> page, String uid, LocalDateTime start, LocalDateTime end);
}