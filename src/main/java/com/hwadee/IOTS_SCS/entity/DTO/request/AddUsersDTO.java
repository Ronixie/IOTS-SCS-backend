package com.hwadee.IOTS_SCS.entity.DTO.request;

import lombok.Data;

import java.util.List;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: AddUsersRequest
* @Package: com.hwadee.IOTS_SCS.entity.DTO.request
* @Description: 管理员添加用户的传递类
* @author qiershi
* @date 2025/7/2 9:06
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class AddUsersDTO {
    private List<String> names;
    private List<String> identities;
    private List<String> phones;
    private String role;
}