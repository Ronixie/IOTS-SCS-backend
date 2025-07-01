package com.hwadee.IOTS_SCS.entity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwadee.IOTS_SCS.entity.POJO.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentMapper extends BaseMapper<User> {
    User getStudentById(@Param("studentId") int id);
    User getStudentByName(@Param("studentName") String name);
}
