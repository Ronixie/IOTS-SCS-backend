package com.hwadee.IOTS_SCS.controller;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.CourseInfoDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.CourseSimpleDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.ProgressDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Course;
import com.hwadee.IOTS_SCS.entity.POJO.Lesson;
import com.hwadee.IOTS_SCS.service.CourseService;
import com.hwadee.IOTS_SCS.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping()
    public CommonResult<List<CourseSimpleDTO>> getAllCourses(
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestHeader("Authorization") String token) {
        String uidFromToken = jwtUtil.getUidFromToken(token);
        return courseService.getAllCourse(status, uidFromToken);
    }

    @GetMapping("/{course_id}")
    public CommonResult<CourseInfoDTO> getCourseInfo(
            @PathVariable("course_id") String courseId,
            @RequestHeader("Authorization") String token) {
        String uidFromToken = jwtUtil.getUidFromToken(token);
        return courseService.getCourseInfo(courseId, uidFromToken);
    }

    @GetMapping("/{course_id}/lessons")
    public CommonResult<List<Lesson>> getCourseLessons(
            @PathVariable("course_id") String courseId) {
        return courseService.getCourseLessons(courseId);
    }

    @GetMapping("/{course_id}/lessons/{lesson_id}")
    public CommonResult<Lesson> getCourseLessonInfo(
            @PathVariable("lesson_id") String lessonId) {
        return courseService.getCourseLessonInfo(lessonId);
    }

    @PostMapping("/{course_id}/lessons/{lesson_id}")
    public CommonResult<?> videoProgress(
            @PathVariable("lesson_id") String lessonId,
            int currentTime,
            @RequestHeader("Authorization") String token) {
        String uidFromToken = jwtUtil.getUidFromToken(token);
        courseService.videoProgress(lessonId,uidFromToken,currentTime);
        return CommonResult.success();
    }

    @PostMapping("/{course_id}/lessons/{lesson_id}/status")
    public CommonResult<Map<String, Integer>> updateLessonStatus(
            @PathVariable("lesson_id") String lessonId,
            @PathVariable("course_id") String courseId,
            @RequestHeader("Authorization") String token) {
        String uidFromToken = jwtUtil.getUidFromToken(token);
        return courseService.updateLessonStatus(lessonId, courseId, uidFromToken);
    }

    @GetMapping("/{course_id}/progress")
    public CommonResult<List<ProgressDTO>> getCourseProgress(
            @PathVariable("course_id") String courseId,
            @RequestHeader("Authorization") String token) {
        String uidFromToken = jwtUtil.getUidFromToken(token);
        return courseService.getCourseProgress(uidFromToken, courseId);
    }

    @GetMapping("/suggestion")
    public CommonResult<?> generateSuggestion(
            @RequestHeader("Authorization") String token) throws NoApiKeyException, InputRequiredException { String uidFromToken = jwtUtil.getUidFromToken(token);
        return courseService.generateSuggestion(uidFromToken);
    }
}