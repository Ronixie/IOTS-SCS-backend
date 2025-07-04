package com.hwadee.IOTS_SCS.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.LogDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.UsersAddedDTO;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import com.hwadee.IOTS_SCS.mapper.AdminMapper;
import com.hwadee.IOTS_SCS.service.AdminService;
import com.hwadee.IOTS_SCS.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: AdminServiceImpl
* @Package: com.hwadee.IOTS_SCS.service.impl
* @Description: 管理员服务的实现
* @author qiershi
* @date 2025/7/2 8:28
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private LogService logService;

    @Override
    public CommonResult<List<UsersAddedDTO>> addNewUsers(List<String> names, List<String> identities, List<String> phones, String role) {
        List<UsersAddedDTO> responses = new ArrayList<>();

        for(int i = 0; i < names.size(); i++) {
            User user = new User();
            user.setName(names.get(i));
            user.setAccount(identities.get(i));
            user.setIdentity(identities.get(i));
            user.setPhone(phones.get(i));
            user.setRole(role);
            user.setPassword("123456");

            adminMapper.insertUser(user);
            responses.add(new UsersAddedDTO(identities.get(i), user.getUid()));
        }

        return CommonResult.success(responses);
    }

    @Override
    public CommonResult<String> resetPassword(String uid) {
        adminMapper.resetPassword(uid);
        return CommonResult.success("123456");
    }

    @Override
    public CommonResult<IPage<LogDTO>> getLogs(String uid, String period) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start;
        if("weekly".equals(period)) start = end.minusWeeks(1);
        else if("monthly".equals(period)) start = end.minusMonths(1);
        else if("yearly".equals(period)) start = end.minusYears(1);
        else return CommonResult.error(404,"时间跨度不合法");

        IPage<LogDTO> result = logService.getUidLogs(uid, start, end);

        return CommonResult.success(result);
    }
}