package com.hwadee.IOTS_SCS.entity.POJO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Post {
    private Long postId;
    private Long courseId;
    private String title;
    private String content;
    private Long userId;
    private Date createTime;
    private List<FileInfo> files;
    private Long likeCount;
}
