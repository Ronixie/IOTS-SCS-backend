package com.hwadee.IOTS_SCS.service.impl;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.CourseDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Course;
import com.hwadee.IOTS_SCS.entity.mapper.CourseMapper;
import com.hwadee.IOTS_SCS.service.CourseService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @ProjectName: smart_study
* @Title: CourseServiceImpl
* @Package: com.csu.smartstudy.service.impl
* @Description: 课程服务类的实现
* @author qiershi
* @date 2025/7/1 8:28
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public CommonResult<List<CourseDTO>> getAllCourse(String status, String account) {
        IPage<CourseDTO> allCourses = new Page<>();
        if("all".equals(status.toLowerCase())){
            courseMapper.getAllCourses(allCourses, account);
        } else {
            courseMapper.getAllCoursesByStatus(allCourses, status, account);
        }
        return CommonResult.successPageData(allCourses);
    }

    @Override
    public CommonResult<Course> getCourseInfo(String course_id) {
        Course course = courseMapper.selectCourseInfo(course_id);
        if (course == null) {
            return CommonResult.error(404,"课程不存在");
        }
        return CommonResult.success(course);
    }
}