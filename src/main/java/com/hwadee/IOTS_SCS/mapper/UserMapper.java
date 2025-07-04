package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwadee.IOTS_SCS.entity.POJO.User;

public interface UserMapper extends BaseMapper<User> {
    User getAccountUser(String account);
    User getUidUser(String uid);
    int update(User user);
}