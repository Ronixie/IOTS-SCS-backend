package com.hwadee.IOTS_SCS.service;


import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.request.UserDTO;

public interface UserService {
    CommonResult<Object> login(String value, String key, int identity);
    CommonResult<Object> logout();
    CommonResult<Object> getUserInfo(String uid);
    CommonResult<Object> updateUser(String uid, UserDTO user);
}
