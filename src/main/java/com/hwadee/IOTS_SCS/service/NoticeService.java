package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.entity.DTO.request.CreateNoticeReq;
import com.hwadee.IOTS_SCS.entity.DTO.response.NoticeDetailDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.NoticeListDTO;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface NoticeService {
    //创建通知
    void createNotice(CreateNoticeReq createNoticeReq);

    //获取通知列表
    List<NoticeListDTO> getNoticeList(Long userId);

    //设置通知已读
    void setNoticeRead(Long noticeId, Long userId);

    //获取通知详情
    NoticeDetailDTO getNoticeDetail(Long noticeId);

    //定时发送通知
    @Scheduled(fixedRate = 60000)
    void sendNotice();

    void sendNoticeToMail(Long noticeId);
}
