package org.csu.ai.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天历史视图对象
 */
@Data
@Builder
public class ChatHistoryVO {
    /**
     * 聊天ID
     */
    private String chatId;
    /**
     * 聊天标题
     */
    private String title;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}