package org.csu.homework.enums;

import lombok.Getter;

@Getter
public enum AssignmentStatus {
    UNFINISHED("未完成"),
    SUBMITTED("已提交"),
    GRADED("已评分"),
    REJECTED("已驳回");
    final String value;

    AssignmentStatus(String value) {
        this.value = value;
    }
}
