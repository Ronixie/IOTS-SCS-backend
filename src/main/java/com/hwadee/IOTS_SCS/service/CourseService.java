package com.hwadee.IOTS_SCS.service;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.CourseInfoDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.CourseSimpleDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.ProgressDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Course;
import com.hwadee.IOTS_SCS.entity.POJO.Lesson;

import java.util.List;
import java.util.Map;

public interface CourseService {
    CommonResult<List<CourseSimpleDTO>> getAllCourse(String status, String uid);
    CommonResult<CourseInfoDTO> getCourseInfo(String courseId, String uid);
    CommonResult<List<Lesson>> getCourseLessons(String courseId);
    CommonResult<Lesson> getCourseLessonInfo(String lessonId);
    CommonResult<Map<String, Integer>> updateLessonStatus(String lessonId, String courseId, String uid);
    CommonResult<?> generateSuggestion(String uid) throws NoApiKeyException, InputRequiredException;
    CommonResult<List<ProgressDTO>> getCourseProgress(String uid, String courseId);
    void videoProgress(String lessonId, String uid, int currentTime);
    void addStudentCourse(String uid, String courseId);
}
