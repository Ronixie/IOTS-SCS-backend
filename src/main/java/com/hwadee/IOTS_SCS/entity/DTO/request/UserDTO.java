package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: UserDTO
* @Package: com.hwadee.IOTS_SCS.entity.DTO
* @Description: 用户更新的传递类
* @author qiershi
* @date 2025/7/1 14:09
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class UserDTO {
    private String phone;
    private String email;
    private String password;
    private String gender;
    private Integer age;
    private String avatarUrl;
}