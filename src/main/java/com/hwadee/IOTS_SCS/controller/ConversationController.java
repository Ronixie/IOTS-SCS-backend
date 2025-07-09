package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.entity.DTO.*;
import com.hwadee.IOTS_SCS.entity.POJO.Conversation;
import com.hwadee.IOTS_SCS.service.impl.ConversationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
    @Autowired
    private ConversationServiceImpl conversationService;

    // 创建或获取会话
    @PostMapping
    public ResponseEntity<Long> createConversation(@RequestBody CreateConversationReq req) {
        Conversation conv = conversationService.createOrGetConversation(
                req.getUserAId(), req.getUserBId()
        );
        return ResponseEntity.ok(conv.getConversationId());
    }

    // 获取用户会话列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationDTO>> getUserConversations(
            @PathVariable Long userId
    ) {
        List<ConversationDTO> conversations = conversationService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    // 删除会话
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Long conversationId
    ) {
        conversationService.deleteConversation(conversationId);
        return ResponseEntity.ok().build();
    }
}

