package com.hwadee.IOTS_SCS.entity.DTO.request;

import lombok.Data;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: LoginDTO
* @Package: com.hwadee.IOTS_SCS.entity.DTO.request
* @Description: 登录dto
* @author qiershi
* @date 2025/7/3 12:06
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class AuthLoginDTO {
    private String way;
    /**
     * 0 - Admin, 1 - Student, 2 - Teacher
     */
    private String role;
    private String username;
    private String password;
}