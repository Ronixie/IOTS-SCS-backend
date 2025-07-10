package com.hwadee.IOTS_SCS.entity.DTO;

import com.hwadee.IOTS_SCS.entity.POJO.FileInfo;
import lombok.Data;

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
    private List<FileInfo> files;
    private Long likeCount;
    private List<ReplyDTO> replies;
}
