package com.hwadee.IOTS_SCS.service.impl;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.CourseDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Course;
import com.hwadee.IOTS_SCS.entity.POJO.Enrollment;
import com.hwadee.IOTS_SCS.entity.POJO.Lesson;
import com.hwadee.IOTS_SCS.entity.POJO.Progress;
import com.hwadee.IOTS_SCS.mapper.CourseMapper;
import com.hwadee.IOTS_SCS.service.CourseService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwadee.IOTS_SCS.util.AIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     *
     * @param status 所查看的课程状态
     * @param uid 用户id
     * @return 分页课程信息
     */
    @Override
    public CommonResult<List<CourseDTO>> getAllCourse(String status, String uid) {
        IPage<CourseDTO> allCourses = new Page<>();
        if("all".equals(status.toLowerCase())){
            courseMapper.getAllCourses(allCourses, uid);
        } else {
            courseMapper.getAllCoursesByStatus(allCourses, status, uid);
        }
        return CommonResult.successPageData(allCourses);
    }

    /**
     *
     * @param course_id 课程id
     * @param uid 用户id
     * @return 课程的详细信息
     */
    @Override
    public CommonResult<Course> getCourseInfo(String course_id, String uid) {

        Course course = courseMapper.getCourseInfo(course_id);
        if (course == null) {
            return CommonResult.error(404,"课程不存在");
        }
        return CommonResult.success(course);
    }

    /**
     *
     * @param courseId 课程id
     * @return 该课程的全部课时
     */
    @Override
    public CommonResult<List<Lesson>> getCourseLessons(String courseId) {
        IPage<Lesson> courseLessons = new Page<>();
        return CommonResult.successPageData(courseMapper.getCourseLessons(courseLessons, courseId));
    }

    /**
     *
     * @param lessonId 课时id
     * @return 课时的详细信息
     */
    @Override
    public CommonResult<Lesson> getCourseLessonInfo(String lessonId) {
        return CommonResult.success(courseMapper.getCourseLessonInfo(lessonId));
    }

    @Override
    public void videoProgress(String lessonId, String uid, int currentTime) {
        courseMapper.updateVideoProgress(lessonId,uid,currentTime);
    }

    /**
     *
     * @param lessonId 课时id
     * @param uid 用户id
     * @return 标记课时完成
     */
    @Override
    public CommonResult<Map<String,Integer>> updateLessonStatus(String lessonId, String courseId, String uid) {
        courseMapper.completeLesson(lessonId, uid);
        int progress = courseMapper.updateProgress(uid, courseId);
        Map<String, Integer> progressMap = new HashMap<>();
        progressMap.put("progress", progress);
        return CommonResult.success(progressMap);
    }

    @Override
    public CommonResult<String> generateSuggestion(String uid) throws NoApiKeyException, InputRequiredException {

        LocalDateTime time = LocalDateTime.now().minusMonths(3);
        // 查数据库，获得数据
        List<Enrollment> enrollment = courseMapper.selectEnrollment(new Page<>(), uid, time).getRecords();
        List<Progress> progress = courseMapper.selectProgress(new Page<>(), uid, time).getRecords();

        String suggestion = AIUtil.callWithMessage(enrollment.toString() + progress.toString());

        return CommonResult.success(suggestion);
    }

    /**
     * 暂不使用
     * @param uid 用户id
     * @param courseId 课程id
     */
    @Override
    public void addStudentCourse(String uid, String courseId) {
        courseMapper.courseSelect(uid, courseId);

        IPage<Lesson> lessons = new Page<>();
        courseMapper.getCourseLessons(lessons , courseId);
        for(Lesson lesson : lessons.getRecords()){
            courseMapper.addStudentLesson(uid, lesson);
        }

        courseMapper.courseProgress(uid, courseId, lessons.getTotal(), LocalDateTime.now());
    }

}