package com.hwadee.IOTS_SCS.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwadee.IOTS_SCS.entity.DTO.response.LogDTO;
import com.hwadee.IOTS_SCS.entity.POJO.ApiAccessLog;
import com.hwadee.IOTS_SCS.mapper.LogMapper;
import com.hwadee.IOTS_SCS.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: LogServiceImpl
* @Package: com.hwadee.IOTS_SCS.service.impl
* @Description: 日志记录的实现
* @author qiershi
* @date 2025/7/3 13:22
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public int log(ApiAccessLog log) {
        return logMapper.insert(log);
    }

    @Override
    public IPage<LogDTO> getUidLogs(String uid, LocalDateTime start, LocalDateTime end) {
        IPage<LogDTO> page = new Page<>();
        logMapper.getUidLogs(page, uid, start, end);
        return page;
    }

}