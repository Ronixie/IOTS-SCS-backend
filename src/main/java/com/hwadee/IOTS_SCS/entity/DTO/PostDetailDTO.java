package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

import java.io.File;
import java.util.Date;
import java.util.List;

@Data
public class PostDetailDTO {
    private Long postId;
    private String title;
    private String content;
    private Long userId;
    private String userName;
    private String avatar;
    private Long courseId;
    private String courseName;
    private Date createTime;
    private List<File> files;
    private List<ReplyDTO> replies; // 回复列表
}
