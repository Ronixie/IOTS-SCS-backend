package com.hwadee.IOTS_SCS.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.LogDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.UsersAddedDTO;

import java.util.List;

public interface AdminService {
    CommonResult<List<UsersAddedDTO>> addNewUsers(List<String> names, List<String> identities, List<String> phones, String Role);
    CommonResult<String> resetPassword(String uid);
    CommonResult<IPage<LogDTO>> getLogs(String uid, String period);
}
