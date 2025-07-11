package org.csu.exam.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventTodoVO {
    String id;
    String type;
    String title;
    LocalDateTime endTime;
    Long courseId;
}
