package com.hwadee.IOTS_SCS.mapper;

import com.hwadee.IOTS_SCS.entity.POJO.Notice;

import java.util.List;

public interface NoticeMapper {
    void insert(Notice notice);

    List<Notice> getNoticeListByUserId(Long userId);

    //仅获取已发送的通知
    Notice getNoticeById(Long noticeId);

    void update(Notice notice);

    List<Notice> getNoticeListByIsSend(boolean b);
}
