// 文件路径: com/hwadee/IOTS_SCS/dto/CreateNoteDTO.java
package com.hwadee.IOTS_SCS.entity.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateNoteDTO {
    private String courseId;
    private String lessonId;
    private String title;
    private String content;
}
