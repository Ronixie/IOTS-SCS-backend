package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hwadee.IOTS_SCS.entity.DTO.response.*;
import com.hwadee.IOTS_SCS.entity.POJO.Course;
import com.hwadee.IOTS_SCS.entity.POJO.Enrollment;
import com.hwadee.IOTS_SCS.entity.POJO.Lesson;
import com.hwadee.IOTS_SCS.entity.POJO.Progress;

import java.time.LocalDateTime;

public interface CourseMapper extends BaseMapper<Course> {
    IPage<CourseSimpleDTO> getAllCoursesByStatus(IPage<CourseSimpleDTO> page, String status, String uid);
    IPage<CourseSimpleDTO> getAllCourses(IPage<CourseSimpleDTO> page, String uid);
    CourseInfoDTO getCourseInfo(String courseId, String uid);
    IPage<Lesson> getCourseLessons(IPage<Lesson> page, String courseId);
    Lesson getCourseLessonInfo(String lessonId);
    int completeLesson(String lessonId, String uid);
    int courseSelect(String uid, String courseId);
    int addStudentLesson(String uid, Lesson lesson);
    int courseProgress(String uid, String courseId, Long totalLessons, LocalDateTime createAt);
    int updateProgress(String uid , String courseId);
    int updateVideoProgress(String lessonId, String uid, int currentTime);
    IPage<Enrollment> selectEnrollment(IPage<Enrollment> page, String uid, LocalDateTime fromTime);
    IPage<Progress> selectProgress(IPage<Progress> page, String uid, LocalDateTime fromTime);
    IPage<ProgressDTO> getCourseProgress(IPage<ProgressDTO> page, String uid, String courseId);

    String getCourseName(String courseId);
    IPage<LessonResDTO> getRes(IPage<LessonResDTO> page, String courseId);
    IPage<LessonVideoDTO> getVideo(IPage<LessonVideoDTO> page, String courseId);

}