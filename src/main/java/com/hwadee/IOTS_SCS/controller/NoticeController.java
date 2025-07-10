package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.entity.DTO.request.CreateNoticeReq;
import com.hwadee.IOTS_SCS.entity.DTO.response.NoticeDetailDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.NoticeListDTO;
import com.hwadee.IOTS_SCS.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 创建通知
    @PostMapping("/create")
    public void createNotice(@RequestBody CreateNoticeReq createNoticeReq) {
        noticeService.createNotice(createNoticeReq);
    }

    // 获取通知列表
    @GetMapping("/list")
    public List<NoticeListDTO> getNoticeList(@RequestParam Long userId) {
        return noticeService.getNoticeList(userId);
    }

    // 设置通知已读
    @PostMapping("/read")
    public void setNoticeRead(@RequestParam Long noticeId, @RequestParam Long userId) {
        noticeService.setNoticeRead(noticeId, userId);
    }

    // 获取通知详情
    @GetMapping("/detail")
    public NoticeDetailDTO getNoticeDetail(@RequestParam Long noticeId) {
        return noticeService.getNoticeDetail(noticeId);
    }
}

