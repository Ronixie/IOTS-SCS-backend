package com.hwadee.IOTS_SCS.service.impl;

import com.hwadee.IOTS_SCS.entity.DTO.request.CreateNoticeReq;
import com.hwadee.IOTS_SCS.entity.DTO.response.NoticeDetailDTO;
import com.hwadee.IOTS_SCS.entity.DTO.response.NoticeListDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Notice;
import com.hwadee.IOTS_SCS.mapper.CourseMapper;
import com.hwadee.IOTS_SCS.mapper.NoticeMapper;
import com.hwadee.IOTS_SCS.mapper.UserMapper;
import com.hwadee.IOTS_SCS.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeMapper noticeMapper;

//    @Autowired
//    private EmailService emailService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseMapper courseMapper;

    //创建通知
    @Override
    public void createNotice(CreateNoticeReq createNoticeReq) {
        Notice notice = new Notice();
        notice.setCourseId(createNoticeReq.getCourseId());
        notice.setNoticeTitle(createNoticeReq.getTitle());
        notice.setNoticeContent(createNoticeReq.getContent());
        notice.setUserId(createNoticeReq.getUserId());
        notice.setNoticeTime(createNoticeReq.getNoticeTime());
        notice.setReceiverIds(createNoticeReq.getReceiverIds());
        notice.setIsSend(false);
        noticeMapper.insert(notice);
    }

    //获取通知列表
    @Override
    public List<NoticeListDTO> getNoticeList(Long userId) {
        List<Notice> noticeList = noticeMapper.getNoticeListByUserId(userId);

        return noticeList.stream().map(notice -> {
            NoticeListDTO noticeListDTO = new NoticeListDTO();
            noticeListDTO.setNoticeId(notice.getNoticeId());
            noticeListDTO.setTitle(notice.getNoticeTitle());
            noticeListDTO.setPreview(notice.getNoticeContent().substring(0, 10));
            noticeListDTO.setCourseId(notice.getCourseId());
            noticeListDTO.setCourseName(courseMapper.getCourseName(String.valueOf(notice.getCourseId())));
            noticeListDTO.setNoticeTime(notice.getNoticeTime());
            noticeListDTO.setIsRead(notice.getReadUserIds().contains(userId));
            return noticeListDTO;
        }).collect(Collectors.toList());
    }

    //设置通知已读
    @Override
    public void setNoticeRead(Long noticeId, Long userId) {
        Notice notice = noticeMapper.getNoticeById(noticeId);
        notice.getReadUserIds().add(userId);
        noticeMapper.update(notice);
    }

    //获取通知详情
    @Override
    public NoticeDetailDTO getNoticeDetail(Long noticeId) {
        Notice notice = noticeMapper.getNoticeById(noticeId);

        setNoticeRead(noticeId, notice.getUserId());

        NoticeDetailDTO noticeDetailDTO = new NoticeDetailDTO();
        noticeDetailDTO.setNoticeId(notice.getNoticeId());
        noticeDetailDTO.setCourseId(notice.getCourseId());
        noticeDetailDTO.setCourseName(courseMapper.getCourseName(String.valueOf(notice.getCourseId())));
        noticeDetailDTO.setUserId(notice.getUserId());
        noticeDetailDTO.setUserName(userMapper.getUserName(String.valueOf(notice.getUserId())));
        noticeDetailDTO.setAvatar(userMapper.getUserAvatar(String.valueOf(notice.getUserId())));
        noticeDetailDTO.setNoticeTitle(notice.getNoticeTitle());
        noticeDetailDTO.setNoticeContent(notice.getNoticeContent());
        noticeDetailDTO.setNoticeTime(notice.getNoticeTime());
        noticeDetailDTO.setReceiverIds(notice.getReceiverIds());
        noticeDetailDTO.setReadUserIds(notice.getReadUserIds());

        return noticeDetailDTO;
    }

    //定时发送通知
    @Override
    @Scheduled(fixedRate = 60000)
    public void sendNotice() {
        List<Notice> noticeList = noticeMapper.getNoticeListByIsSend(false);
        for (Notice notice : noticeList) {
            if(notice.getNoticeTime() == null || notice.getNoticeTime().before(new Date())) {
                notice.setIsSend(true);
                sendNoticeToMail(notice.getNoticeId());
            }
            noticeMapper.update(notice);
        }
    }

    @Override
    public void sendNoticeToMail(Long noticeId){
//        Notice notice = noticeMapper.getNoticeById(noticeId);
//
//        User sender = userMapper.getUidUser(String.valueOf(notice.getUserId()));
//
//        for(Long receiverId : notice.getReceiverIds()){
//            User receiver = userMapper.getUidUser(String.valueOf(receiverId));
//            if(receiver.getEmail() != null && !receiver.getEmail().isEmpty()){
//                //邮件内容
//                String content = String.format(
//                        "example %s",
//                        "example111"
//                );
//
//                emailService.sendSimpleMessage(receiver.getEmail(), "example subject", content);
//            }
//        }
    }
}
