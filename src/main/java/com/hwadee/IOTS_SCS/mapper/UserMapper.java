package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<User> {
    User getAccountUser(String account);
    User getUidUser(String uid);
    int update(User user);
    String getPassword(String uid);
    int updatePassword(String password, String uid);

    String getUserName(String uid);
    String getUserAvatar(String uid);

    // 新增方法：更新用户头像URL
    @Update("UPDATE users SET avatar_url = #{avatarUrl} WHERE uid = #{uid}")
    int updateUserAvatar(@Param("uid") Long uid, @Param("avatarUrl") String avatarUrl);
}