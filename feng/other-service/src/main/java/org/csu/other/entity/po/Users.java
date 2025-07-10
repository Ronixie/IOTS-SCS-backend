package org.csu.other.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2025-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    @TableId(value = "uid")
    private Long uid;

    /**
     * 账号
     */
    private String account;

    /**
     * 学号(工号)
     */
    private String identity;

    /**
     * 密码哈希值
     */
    @JsonIgnore
    private String passwordHash;

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
     * 角色, 如"student", "teacher", "admin"等
     */
    private String role;

    /**
     * 头像url
     */
    private String avatorUrl;


}
