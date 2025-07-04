package com.hwadee.IOTS_SCS.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.request.AddCoursesDTO;
import com.hwadee.IOTS_SCS.entity.DTO.request.AddUsersDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.LogDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.UsersAddedDTO;
import com.hwadee.IOTS_SCS.service.AdminService;
import com.hwadee.IOTS_SCS.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: AdminController
* @Package: com.hwadee.IOTS_SCS.controller
* @Description: 管理员通道
* @author qiershi
* @date 2025/7/2 8:26
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/users")
    public CommonResult<List<UsersAddedDTO>> addUser(
            @RequestBody AddUsersDTO users) {
        List<String> names = users.getNames();
        List<String> identities = users.getIdentities();
        List<String> phones = users.getPhones();
        String role = users.getRole();
        return adminService.addNewUsers(names, identities, phones, role);
    }

//    @PostMapping("/courses")
//    public CommonResult<List<UsersAddedDTO>> addCourse(
//            @RequestBody List<AddCoursesDTO> courses) {
//        return adminService.addNewCourses();
//    }

//    @Autowired
//    private CourseService courseService;
//
//    @PostMapping("/")
//    public CommonResult<?> studentSelectCourse() {
//        courseService.addStudentCourse(uid,courseId);
//    }

    @PostMapping("/users/{uid}")
    public CommonResult<String> resetPassword(@PathVariable("uid") String uid) {
        return adminService.resetPassword(uid);
    }

    @GetMapping("/users/{uid}/activity_logs")
    public CommonResult<IPage<LogDTO>> getLogs(
            @PathVariable("uid") String uid,
            @RequestParam("period") String period) {
        return adminService.getLogs(uid, period);
    }
}