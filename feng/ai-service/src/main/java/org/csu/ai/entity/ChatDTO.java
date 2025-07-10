package org.csu.ai.entity;

import lombok.Data;

import java.util.List;

/**
 * 聊天数据传输对象
 */
@Data
public class ChatDTO {
    /**
     * 用户输入的提示信息
     */
    private String prompt;

    /**
     * 上传的文件列表
     */
    private List<String> files;
}