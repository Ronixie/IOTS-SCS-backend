package com.hwadee.IOTS_SCS.controller;


import com.hwadee.IOTS_SCS.entity.DTO.response.MessageDTO;
import com.hwadee.IOTS_SCS.entity.DTO.request.SendMessageReq;
import com.hwadee.IOTS_SCS.entity.POJO.Message;
import com.hwadee.IOTS_SCS.service.impl.MessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageServiceImpl messageService;

    // 发送消息
    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody SendMessageReq req) {
        Message message = messageService.sendMessage(req);
        // 转换为DTO
        return ResponseEntity.ok(messageService.convertToDTO(message));
    }

    // 获取会话消息历史
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDTO>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam Long currentUserId // 当前查看的用户
    ) {
        List<MessageDTO> messages = messageService.getConversationMessages(conversationId, currentUserId);
        return ResponseEntity.ok(messages);
    }


}
