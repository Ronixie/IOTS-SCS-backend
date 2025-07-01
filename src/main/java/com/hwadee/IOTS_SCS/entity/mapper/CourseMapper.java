package com.hwadee.IOTS_SCS.entity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hwadee.IOTS_SCS.entity.POJO.Course;
import com.hwadee.IOTS_SCS.entity.DTO.CourseDTO;

/**
* @ProjectName: smart_study
* @Title: CourseMapper
* @Package: com.csu.smartstudy.mapper
* @Description: 课程相关的数据库操作
* @author qiershi
* @date 2025/7/1 8:12
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
public interface CourseMapper extends BaseMapper<Course> {
    IPage<CourseDTO> getAllCoursesByStatus(IPage<CourseDTO> page, String status, String account);
    IPage<CourseDTO> getAllCourses(IPage<CourseDTO> page, String account);
    Course selectCourseInfo(String course_id);
}