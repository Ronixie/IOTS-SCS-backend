package com.hwadee.IOTS_SCS.entity.DTO;

import com.hwadee.IOTS_SCS.entity.POJO.FileInfo;
import lombok.Data;

import java.util.List;

@Data
public class CreateDiscussionPostReq {
    private String title;
    private String content;
    private Long courseId;       // 讨论帖关联课程ID
    private Long userId;
    private List<FileInfo> files;
}
