package com.hwadee.IOTS_SCS.entity.POJO;

public enum MessageType {
    TEXT(1, "文本"),
    IMAGE(2, "图片"),
    VIDEO(3, "视频"),
    FILE(4, "文件"),
    ;

    MessageType(int i, String typeName) {
    }
}
