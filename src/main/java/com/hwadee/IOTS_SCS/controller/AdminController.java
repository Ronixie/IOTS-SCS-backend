package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.request.AddUsersRequest;
import com.hwadee.IOTS_SCS.entity.DTO.response.UsersAddedResponse;
import com.hwadee.IOTS_SCS.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: AdminController
* @Package: com.hwadee.IOTS_SCS.controller
* @Description: 管理员通道
* @author qiershi
* @date 2025/7/2 8:26
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/users")
    public CommonResult<List<UsersAddedResponse>> addUser(
            @RequestBody AddUsersRequest users) {
        return adminService.addNewUsers(users.getUsernames(),users.getRole());
    }

    @PostMapping("/users/{uid}")
    public CommonResult<String> resetPassword(@PathVariable("uid") String uid) {
        return adminService.resetPassword(uid);
    }
}