package com.hwadee.IOTS_SCS.entity.vo;


import lombok.Data;

@Data
public class UserVO {

    /**
     * 用户唯一标识
     */
    private Long uid;

    /**
     * 学号(工号)
     */
    private String identity;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 头像url
     */
    private String avatarUrl;

}
