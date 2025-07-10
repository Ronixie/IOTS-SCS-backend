package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

@Data
public class Evaluation
{
    private Long evaluationId;
    private Long courseId;      // 关联课程ID（保留原字段）
    private String courseName;  // 课程名称（新增）
    private Long teacherId;     // 授课教师（新增）
    private int contentEvaluation;
    private int serviceEvaluation;
    private int attitudeEvaluation;
    private int effectEvaluation;
    private String evaluationContent;

}
