package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: Lesson
* @Package: com.hwadee.IOTS_SCS.entity.POJO
* @Description: 课时类
* @author qiershi
* @date 2025/7/1 13:46
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Data
public class Lesson {
    private long lessonId;
    private long courseId;
    private String lessonTitle;
    private String lessonType;
    private int order;
    private String contentUrl;
    private String textContent;
}