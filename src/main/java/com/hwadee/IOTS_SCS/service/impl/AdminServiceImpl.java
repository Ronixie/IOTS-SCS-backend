package com.hwadee.IOTS_SCS.service.impl;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.UsersAddedResponse;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import com.hwadee.IOTS_SCS.mapper.AdminMapper;
import com.hwadee.IOTS_SCS.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public CommonResult<List<UsersAddedResponse>> addNewUsers(List<String> usernames, String role) {
        List<UsersAddedResponse> responses = new ArrayList<>();

        for(String username : usernames) {
            User user = new User();
            user.setName(username);
            user.setAccount("11111");
            user.setIdentity("11111");
            user.setRole(role);
            user.setPassword("123456");
            adminMapper.insertUser(user);

            responses.add(new UsersAddedResponse(username, user.getUid()));
        }

        return CommonResult.success(responses);
    }

    @Override
    public CommonResult<String> resetPassword(String uid) {
        adminMapper.resetPassword(uid);
        return CommonResult.success("123456");
    }
}