package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    protected Long uid;
    protected String account;
    protected String password;

    protected String email;
    protected String phone;
    protected String name;
    protected String role;
    //学号(工号)
    protected String identity;
    protected int age;
    protected String gender;
    protected String college;
    protected String major;
    protected String avatarUrl;

}
