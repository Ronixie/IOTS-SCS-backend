package com.hwadee.IOTS_SCS.entity.POJO;

public enum MessageType {
    TEXT(1, "文本"),
    IMAGE(2, "图片"),
    FILE(3, "文件"),
    VOICE(4, "语音"),
    VIDEO(5, "视频"),
    LOCATION(6, "位置"),
    RICH_TEXT(7, "富文本");

    MessageType(int i, String typeName) {
    }
}
