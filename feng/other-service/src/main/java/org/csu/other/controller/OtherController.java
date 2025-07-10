package org.csu.other.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.csu.other.entity.po.Users;
import org.csu.other.entity.vo.UserVO;
import org.csu.other.service.ICoursesService;
import org.csu.other.service.IStudentCourseService;
import org.csu.other.service.IUsersService;
import org.csu.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OtherController {
    private final IStudentCourseService studentCourseService;
    private final IUsersService userService;
    private final ICoursesService coursesService;
    // 获取课程对应的所有学生id
    @GetMapping("/courses/students/{courseId}")
    public Result<List<Long>> getStudentIdsByCourseId(@PathVariable("courseId") Long courseId) {
        return Result.success(studentCourseService.getStudentIdsByCourseId(courseId));
    }
    // 获取用户信息
    @GetMapping("/users/{uid}")
    public Result<UserVO> getUserInfo(@PathVariable("uid") Long uid) {
        return Result.success(BeanUtil.copyProperties(userService.getById(uid),UserVO.class));
    }

    @GetMapping("/courses/name/{courseId}")
    public Result<String> getCourseName(@PathVariable("courseId") Long courseId) {
        return Result.success(coursesService.getCourseName(courseId));
    }

    @GetMapping("/users/courses/{uid}")
    public Result<List<Long>> getCourseIdsByUid(@PathVariable("uid") Long uid) {
        return Result.success(studentCourseService.getCourseIdsByUid(uid));
    }
}
