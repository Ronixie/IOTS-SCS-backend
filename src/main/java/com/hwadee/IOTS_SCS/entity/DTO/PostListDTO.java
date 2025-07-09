package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class PostListDTO {
    private Long postId;
    private String title;
    private String preview;      // 内容预览
    private Long userId;
    private String userName;
    private String avatar;
    private Long courseId;
    private Date createTime;
    private Long replyCount;
}
