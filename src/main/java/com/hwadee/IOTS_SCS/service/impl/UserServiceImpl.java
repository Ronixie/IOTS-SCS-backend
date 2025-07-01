package com.hwadee.IOTS_SCS.service.impl;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import com.hwadee.IOTS_SCS.service.UserService;
import com.hwadee.IOTS_SCS.entity.mapper.StudentMapper;
import com.hwadee.IOTS_SCS.util.JwtUtil;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author qiershi
 * @version V1.0
 * Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
 * @ProjectName:smart_study
 * @Title: UserServiceImpl
 * @Package com.csu.smartstudy.service.impl
 * @Description: 用户服务类的实现
 * @date 2025/6/30 9:14
 */
@Log4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    StudentMapper studentMapper;

    @Autowired
    JwtUtil jwtUtil;

    /**
     *
     * @param value 登录的用户名或学号
     * @param key 密码
     * @param identity 登录者身份
     * @return 登录信息
     */
    @Override
    public CommonResult<Object> login(String value, String key, int identity) {
        HashMap<String, Object> result = new HashMap<>();

        System.out.println("\nvalue=[" + value + "]\nkey=[" + key + "]\nidentity=[" + identity + "]\n");

        User student = studentMapper.getStudentByName(value.trim());
        if (student == null) {
            return CommonResult.error(404,"用户不存在");
        }

        if (key.equals(student.getPassword())) {
            result.put("tokens", jwtUtil.generateToken(value));

            HashMap<String, Object> userInfo = new HashMap<>();
            userInfo.put("uid", student.getUid());
            userInfo.put("account", student.getAccount());
            userInfo.put("role", student.getRole());
            userInfo.put("avatar_url", student.getAvatarUrl());

            result.put("userInfo",userInfo);

            return CommonResult.success(result);
        }
        else {
            return CommonResult.success("密码错误");
        }
    }

    /**
     *
     * @param uid 用户id
     * @return 用户详细信息
     */
    @Override
    public CommonResult<Object> getUserInfo(String uid) {
        return CommonResult.success(studentMapper.getStudentById(Integer.parseInt(uid)));
    }

    /**
     *
     * @param uid 用户id
     * @param user 用户更新信息
     * @return 更新完成信息
     */
    @Override
    public CommonResult<Object> updateUser(String uid, User user) {
        studentMapper.updateById(user);
        return CommonResult.success();
    }

    /**
     *
     * @return 删除用户登录服务器信息
     */
    @Override
    public CommonResult<Object> logout() {
        //登出处理
        return CommonResult.success();
    }

}