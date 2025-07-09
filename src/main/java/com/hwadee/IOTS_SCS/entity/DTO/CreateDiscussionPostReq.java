package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

import java.io.File;
import java.util.Date;
import java.util.List;

@Data
public class CreateDiscussionPostReq {
    private String title;
    private String content;
    private Long courseId;       // 讨论帖关联课程ID
    private Date createTime;
    private Long userId;
    private List<File> files;
}
