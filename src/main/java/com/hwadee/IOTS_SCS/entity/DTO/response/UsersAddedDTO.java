package com.hwadee.IOTS_SCS.entity.DTO.response;

import lombok.Data;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: UsersAddedResponse
* @Package: com.hwadee.IOTS_SCS.entity.DTO.response
* @Description: 管理员添加用户的返回传递类
* @author qiershi
* @date 2025/7/2 9:07
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class UsersAddedDTO {
    private String account;
    private Long uid;

    public UsersAddedDTO(String account, Long uid) {
        this.account = account;
        this.uid = uid;
    }
}