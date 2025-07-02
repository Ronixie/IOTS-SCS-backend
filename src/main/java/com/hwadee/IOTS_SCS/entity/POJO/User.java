package com.hwadee.IOTS_SCS.entity.POJO;

import com.hwadee.IOTS_SCS.entity.DTO.request.UserDTO;
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

    public User() {
    }

    public void update(UserDTO dto) {
        if (dto.getPassword() != null) this.password = dto.getPassword();
        if (dto.getPhone() != null) this.phone = dto.getPhone();
        if (dto.getEmail() != null) this.email = dto.getEmail();
        if (dto.getGender() != null) this.gender = dto.getGender();
        if (dto.getAge() != null) this.age = dto.getAge();
        if (dto.getAvatarUrl() != null) this.avatarUrl = dto.getAvatarUrl();
    }

}
