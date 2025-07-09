package com.hwadee.IOTS_SCS.entity.DTO;

import java.io.File;
import java.util.Date;
import java.util.List;

@lombok.Data
public class CreateSharingPostReq {
    private String title;
    private String content;
    private Date createTime;
    private Long userId;
    private List<File> files;
}
