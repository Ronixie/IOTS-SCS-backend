package com.hwadee.IOTS_SCS.entity.DTO.request;

import lombok.Data;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: UpdatePasswordDTO
* @Package: com.hwadee.IOTS_SCS.entity.DTO.request
* @Description: 修改密码
* @author qiershi
* @date 2025/7/8 19:45
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class UserChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
}