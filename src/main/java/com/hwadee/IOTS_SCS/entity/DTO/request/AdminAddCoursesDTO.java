package com.hwadee.IOTS_SCS.entity.DTO.request;

import com.hwadee.IOTS_SCS.entity.POJO.Lesson;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: AddCoursesDTO
* @Package: com.hwadee.IOTS_SCS.entity.DTO.request
* @Description: 添加课程的传递类
* @author qiershi
* @date 2025/7/3 19:49
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class AdminAddCoursesDTO {

    private String courseNames;
    private Long teacherId;
    private int credit;
    private String description;
    private String coverImageUrl;
    private int totalLessons;
    private Date startDate;
    private Date endDate;
    private List<Lesson> lessons;
}