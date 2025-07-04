package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hwadee.IOTS_SCS.entity.POJO.Course;
import com.hwadee.IOTS_SCS.entity.DTO.response.CourseDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Lesson;

import java.time.LocalDateTime;

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
    IPage<CourseDTO> getAllCoursesByStatus(IPage<CourseDTO> page, String status, String uid);
    IPage<CourseDTO> getAllCourses(IPage<CourseDTO> page, String uid);
    Course getCourseInfo(String courseId);
    IPage<Lesson> getCourseLessons(IPage<Lesson> page, String courseId);
    Lesson getCourseLessonInfo(String lessonId);
    int completeLesson(String lessonId, String uid);
    int courseSelect(String uid, String courseId);
    int addStudentLesson(String uid, Lesson lesson);
    int courseProgress(String uid, String courseId, Long totalLessons, LocalDateTime createAt);
    int updateProgress(String uid , String courseId);
    int updateVideoProgress(String lessonId, String uid, int currentTime);
}