package com.hwadee.IOTS_SCS.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hwadee.IOTS_SCS.entity.DTO.response.LogDTO;
import com.hwadee.IOTS_SCS.entity.POJO.ApiAccessLog;

import java.time.LocalDateTime;

public interface LogService {
    int log(ApiAccessLog log);
    IPage<LogDTO> getUidLogs(String uid, LocalDateTime start, LocalDateTime end);
}