package org.csu.ai.memory;

import lombok.RequiredArgsConstructor;
import org.csu.ai.service.IChatMemoryService;
import org.csu.ai.service.impl.GridFsService;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 基于MongoDB实现的聊天记忆存储组件
 */
@Component
@RequiredArgsConstructor
public class MongoDBChatMemory implements ChatMemory {
    private final IChatMemoryService chatMemoryService;
    private final GridFsService gridFsService;

    /**
     * 添加单条消息到指定会话的记忆中
     *
     * @param conversationId 会话ID
     * @param message        要添加的消息对象
     */
    @Override
    public void add(String conversationId, Message message) {
        // 将 Message 转换为实体类并保存到数据库
        String role = message.getMessageType().getValue();
        String content = message.getText();
        org.csu.ai.entity.po.ChatMemory memory = new org.csu.ai.entity.po.ChatMemory(conversationId, role, content);
        if (message instanceof UserMessage userMessage) {
            Collection<Media> media = userMessage.getMedia();
            List<MimeType> types = new ArrayList<>();
            List<byte[]> data = media.stream().map(m -> {
                types.add(m.getMimeType());
                return (byte[])m.getData();
            }).toList();
            List<String> gridFsIds = gridFsService.saveByteArrayListAsMultipleFiles(data, conversationId);
            memory.saveDataList(gridFsIds, types);
        }
        chatMemoryService.save(memory);
    }

    /**
     * 添加多条消息到指定会话的记忆中
     *
     * @param conversationId 会话ID
     * @param messages       要添加的消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            add(conversationId, message);
        }
    }


    /**
     * 从指定会话的记忆中获取最近N条消息
     *
     * @param conversationId 会话ID
     * @return 消息列表
     */
    @Override
    public List<Message> get(String conversationId) {
        List<org.csu.ai.entity.po.ChatMemory> memories = chatMemoryService.findBySessionIdOrderByCreatedAtAsc(conversationId);
        return memories.stream().map(memory -> {
            String role = memory.getRole();
            String content = memory.getContent();

            // 显式地将结果转换为Message接口类型
            return switch (role) {
                case "user" -> {
                    List<org.csu.ai.entity.po.ChatMemory.MetaData> metaData = memory.getDataList();
                    List<Media> media = metaData.stream().
                            map(m ->
                                    new Media(new MimeType(m.getType(), m.getSubtype()),
                                            new ByteArrayResource(gridFsService.getFileAsByteArray(m.getGridFsId()))))
                            .toList();

                    yield (Message) UserMessage.builder().text(content).media(media).build();
                }
                case "assistant" -> (Message) new AssistantMessage(content);
                case "system" -> (Message) new SystemMessage(content);
                default -> null;
            };
        }).filter(Objects::nonNull).toList();
    }

    /**
     * 清除指定会话的所有记忆
     *
     * @param conversationId 会话ID
     */
    @Override
    public void clear(String conversationId) {
        chatMemoryService.deleteBySessionId(conversationId);
    }
}