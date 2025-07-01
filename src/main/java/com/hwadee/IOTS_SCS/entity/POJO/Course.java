package com.hwadee.IOTS_SCS.entity.POJO;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
* @ProjectName: smart_study
* @Title: Course
* @Package: com.csu.smartstudy.entity
* @Description: 课程类
* @author qiershi
* @date 2025/7/1 8:08
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class Course {
    @TableId("course_id")
    private Long id;
    private String courseName;
    private Long teacherId;
    private double credit;
    private String description;
    public String coverImageUrl;

}