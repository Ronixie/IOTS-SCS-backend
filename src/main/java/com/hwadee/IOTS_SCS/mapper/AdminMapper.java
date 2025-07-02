package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<User> {
    User getUserByAccount(String account);
    String getRoleByUid(String uid);
    int insertUser(User user);
    int resetPassword(String uid);
}
