package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.UsersAddedResponse;

import java.util.List;

public interface AdminService {
    CommonResult<List<UsersAddedResponse>> addNewUsers(List<String> usernames, String Role);
    CommonResult<String> resetPassword(String uid);
}
