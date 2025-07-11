package com.hwadee.IOTS_SCS.entity.POJO;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum MessageType {
    TEXT(1, "文本"),
    IMAGE(2, "图片"),
    VIDEO(3, "视频"),
    FILE(4, "文件");

    @EnumValue  // 添加MyBatis-Plus注解
    private final int value;
    private final String typeName;

    // 添加字段赋值
    MessageType(int value, String typeName) {
        this.value = value;
        this.typeName = typeName;
    }

    // 添加getter方法
    public int getValue() {
        return value;
    }

    public String getTypeName() {
        return typeName;
    }
}

