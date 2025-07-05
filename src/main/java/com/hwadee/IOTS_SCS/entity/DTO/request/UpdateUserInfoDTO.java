package com.hwadee.IOTS_SCS.entity.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class UpdateUserInfoDTO {
    @Pattern(regexp = "^[0-9]{11}$", message = "手机号码格式错误")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*.[a-z]{2,}$", message = "邮箱格式错误")
    private String email;

    @Pattern(regexp = "^[a-zA-Z0-9@#$%^&_]{6,}$", message = "密码格式错误")
    private String password;

    private String gender;
    private Integer age;
    private String avatarUrl;
}