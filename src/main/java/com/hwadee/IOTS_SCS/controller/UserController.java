package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.request.AuthLoginDTO;
import com.hwadee.IOTS_SCS.entity.DTO.request.UserChangePasswordDTO;
import com.hwadee.IOTS_SCS.entity.DTO.request.UserUpdateDTO;
import com.hwadee.IOTS_SCS.service.UserService;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


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


    // 用户登录
    @PostMapping("/auth/login")
    public CommonResult<Object> login(@RequestBody AuthLoginDTO dto) {
        String way = dto.getWay();
        String account = dto.getUsername();
        String password = dto.getPassword();
        int role = Integer.parseInt(dto.getRole());

        return userService.login(account, password, role);
    }

    // 获取用户详细信息
    @GetMapping("/users/{uid}")
    public CommonResult<Object> getUser(@PathVariable("uid") String uid) {
        return userService.getUserInfo(uid);
    }

    // 修改用户信息
    @PutMapping(value = "/users/{uid}")
    public CommonResult<Object> updateUser(
            @PathVariable("uid") String uid,
            @RequestBody UserUpdateDTO user) {
        return userService.updateUser(uid, user);
    }

    // 修改用户密码
    @PutMapping(value = "/auth/{uid}")
    public CommonResult<Object> updatePassword(
            @PathVariable("uid") String uid,
            @RequestBody UserChangePasswordDTO dto) {
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();
        return userService.updatePassword(uid, oldPassword, newPassword);
    }

    // 发送用户头像
    @GetMapping("/users/{uid}/avatar")
    public void getAvatar(
            @PathVariable("uid") String uid,
            HttpServletResponse response)
            throws IOException {
        String avatar = userService.getUserAvatar(uid);

        File avatarFile = new File("res/file/" + avatar.substring(35));

        if(!avatarFile.exists()) avatarFile = new File("res/file/default/avatar.png");

        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache");
        Files.copy(avatarFile.toPath(), response.getOutputStream());
        response.flushBuffer();
    }

    @PostMapping("/users/{user_id}/avatar")
    public CommonResult<Object> updateUserAvatar(
            @RequestHeader("Authorization") String token,
            HttpServletRequest request,
            MultipartFile avatar) {
        return userService.updateAvatar(avatar, token, request);
    }

    // 用户登出
    @PostMapping("/auth/logout")
    public CommonResult<Object> logout() {
        return userService.logout();
    }
}
