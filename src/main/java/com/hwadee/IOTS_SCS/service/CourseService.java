package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.CourseDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Course;

import java.util.List;

public interface CourseService {
    CommonResult<List<CourseDTO>> getAllCourse(String status, String account);
    CommonResult<Course> getCourseInfo(String course_id);
}
