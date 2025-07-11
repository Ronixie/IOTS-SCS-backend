package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.request.AuthLoginDTO;
import com.hwadee.IOTS_SCS.entity.DTO.request.UserChangePasswordDTO;
import com.hwadee.IOTS_SCS.entity.DTO.request.UserUpdateDTO;
import com.hwadee.IOTS_SCS.service.UserService;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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

@Controller
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;


    // 用户登录
    @PostMapping("/auth/login")
    @ResponseBody
    public CommonResult<Object> login(@RequestBody AuthLoginDTO dto) {
        String way = dto.getWay();
        String account = dto.getUsername();
        String password = dto.getPassword();
        int role = Integer.parseInt(dto.getRole());

        return userService.login(account, password, role);
    }

    // 获取用户详细信息
    @GetMapping("/users/{uid}")
    @ResponseBody
    public CommonResult<Object> getUser(@PathVariable("uid") String uid) {
        return userService.getUserInfo(uid);
    }

    // 修改用户信息
    @PutMapping(value = "/users/{uid}")
    @ResponseBody
    public CommonResult<Object> updateUser(
            @PathVariable("uid") String uid,
            @RequestBody UserUpdateDTO user) {
        return userService.updateUser(uid, user);
    }

    // 修改用户密码
    @PutMapping(value = "/auth/{uid}")
    @ResponseBody
    public CommonResult<Object> updatePassword(
            @PathVariable("uid") String uid,
            @RequestBody UserChangePasswordDTO dto) {
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();
        return userService.updatePassword(uid, oldPassword, newPassword);
    }

    // 发送用户头像
    @GetMapping("/users/{uid}/avatar")
    public void getAvatar(@PathVariable("uid") String uid, HttpServletResponse response) {
        System.out.println("/----------------------------------/");
        try {
            String avatarFileName = uid + "_avatar.jpg";
            String basePath = System.getProperty("user.dir") + File.separator + "res" + File.separator + "file" + File.separator;
            File avatarFile;

            if (StringUtils.hasText(avatarFileName)) {
                avatarFile = new File(basePath + avatarFileName);
                if (!avatarFile.exists()) {
                    System.out.println("头像不存在，改为默认");
                    avatarFile = new File(basePath + "default" + File.separator + "avatar.png");
                }
            } else {
                System.out.println("未设置头像，使用默认");
                avatarFile = new File(basePath + "default" + File.separator + "avatar.png");
            }

            if (!avatarFile.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("text/plain");
                response.getWriter().write("头像文件不存在");
                return;
            }

            // 设置响应类型
            String fileName = avatarFile.getName().toLowerCase();
            if (fileName.endsWith(".png")) {
                response.setContentType("image/png");
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                response.setContentType("image/jpeg");
            } else if (fileName.endsWith(".gif")) {
                response.setContentType("image/gif");
            } else {
                response.setContentType("application/octet-stream");
            }

            // 禁止缓存
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            // 复制文件到响应流
            try (OutputStream os = response.getOutputStream()) {
                Files.copy(avatarFile.toPath(), os);
                response.flushBuffer();
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/plain");
                response.getWriter().write("服务器错误，无法加载头像");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }



    @PostMapping("/users/{user_id}/avatar")
    @ResponseBody
    public CommonResult<Object> updateUserAvatar(
            @RequestHeader("Authorization") String token,
            HttpServletRequest request,
            MultipartFile avatar) {
        return userService.updateAvatar(avatar, token, request);
    }

    // 用户登出
    @PostMapping("/auth/logout")
    @ResponseBody
    public CommonResult<Object> logout() {
        return userService.logout();
    }
}
