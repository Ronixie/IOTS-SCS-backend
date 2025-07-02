package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.CourseDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Course;
import com.hwadee.IOTS_SCS.entity.POJO.Lesson;

import java.util.List;

public interface CourseService {
    CommonResult<List<CourseDTO>> getAllCourse(String status, String uid);
    CommonResult<Course> getCourseInfo(String courseId, String uid);
    CommonResult<List<Lesson>> getCourseLessons(String courseId);
    CommonResult<Lesson> getCourseLessonInfo(String lessonId);
    CommonResult<String> updateLessonStatus(String lessonId, String uid);

}
