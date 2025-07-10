// 文件路径: com/hwadee/IOTS_SCS/pojo/Note.java
package com.hwadee.IOTS_SCS.entity.POJO;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Note {
    private String id;
    private String courseId;
    private String lessonId;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}