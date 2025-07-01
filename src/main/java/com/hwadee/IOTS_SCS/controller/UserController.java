package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.POJO.User;
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

    /**
     *
     * @param account 登录的用户名或学号
     * @param password 密码
     * @param role 登录者身份
     * @return 登录信息
     */
    @PostMapping("/auth/login")
    public CommonResult<Object> login(@RequestParam("username") String account, String password, String role) {
        return userService.login(account, password, Integer.parseInt(role));
    }

    /**
     *
     * @param uid 用户id
     * @return 用户详细信息
     */
    @GetMapping("/users/{uid}")
    public CommonResult<Object> getUser(@PathVariable("uid") String uid) {
        return userService.getUserInfo(uid);
    }

    @PutMapping("/users/{uid}")
    public CommonResult<Object> updateUser(@PathVariable("uid") String uid, @RequestBody User user) {
        return userService.updateUser(uid, user);
    }

    /**
     *
     * @return 登出, 删除服务器保留的信息
     */
    @PostMapping("/auth/logout")
    public CommonResult<Object> logout() {
        return userService.logout();
    }
}
