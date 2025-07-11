package com.hwadee.IOTS_SCS.service;


import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.request.UserUpdateDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    CommonResult<Object> login(String value, String key, int role);
    CommonResult<Object> logout();
    CommonResult<Object> getUserInfo(String uid);
    CommonResult<Object> updateUser(String uid, UserUpdateDTO user);
    CommonResult<Object> updatePassword(String uid, String oldPassword, String newPassword);
    CommonResult<Object> updateAvatar(MultipartFile file, String token, HttpServletRequest request);
    String getUserAvatar(String uid);
}
