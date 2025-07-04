package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.request.LoginDTO;
import com.hwadee.IOTS_SCS.entity.DTO.request.UpdateUserInfoDTO;
import com.hwadee.IOTS_SCS.service.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @ProjectName: smart_study
 * @Title: UserController
 * @Package controller
 * @Description: 用户控制器
 * @author qiershi
 * @date 2025-6-28 8:59:00
 * @version V1.0
 * Copyright (c) 2025, qiershi2006@163.com All Rights Reserved.
 */

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/auth/login")
    public CommonResult<Object> login(@RequestBody LoginDTO dto) {
        String way = dto.getWay();
        String account = dto.getUsername();
        String password = dto.getPassword();
        int role = Integer.parseInt(dto.getRole());

        return userService.login(account, password, role);
    }

    @GetMapping("/users/{uid}")
    public CommonResult<Object> getUser(@PathVariable("uid") String uid) {
        return userService.getUserInfo(uid);
    }

    @PutMapping(value = "/users/{uid}")
    public CommonResult<Object> updateUser(
            @PathVariable("uid") String uid,
            @RequestBody UpdateUserInfoDTO user) {
        return userService.updateUser(uid, user);
    }

    @PostMapping("/auth/logout")
    public CommonResult<Object> logout() {
        return userService.logout();
    }
}
