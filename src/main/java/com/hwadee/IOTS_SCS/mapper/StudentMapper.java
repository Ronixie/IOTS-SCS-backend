package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<User> {
    int update(User user);
}
