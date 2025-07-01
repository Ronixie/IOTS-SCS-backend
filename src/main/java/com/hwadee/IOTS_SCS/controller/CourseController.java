package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.CourseDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Course;
import com.hwadee.IOTS_SCS.service.CourseService;
import com.hwadee.IOTS_SCS.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @ProjectName: smart_study
* @Title: CourseController
* @Package: com.csu.smartstudy.controller
* @Description: 课程相关的接口
* @author qiershi
* @date 2025/7/1 8:28
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     *
     * @param status 课程状态(可选项)
     * @param token 用于获取用户account
     * @return MyBatis分页
     */
    @GetMapping()
    public CommonResult<List<CourseDTO>> getAllCourses(
            @RequestParam(value="status",required = false,defaultValue = "all") String status,
            @RequestHeader("Authorization") String token) {
        String accountFromToken = jwtUtil.getAccountFromToken(token);
        return courseService.getAllCourse(status, accountFromToken);
    }


    /**
     *
     * @param course_id 需要详细信息的课程id
     * @return 课程详细信息
     */
    @GetMapping("/{course_id}")
    public CommonResult<Course> getCourseInfo(@PathVariable("course_id") String course_id) {
        return courseService.getCourseInfo(course_id);
    }
}