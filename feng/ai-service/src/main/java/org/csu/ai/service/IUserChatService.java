package org.csu.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.ai.entity.po.UserChat;

import java.util.List;

/**
 * <p>
 * 用户id与AIchatId对应表 服务类
 * </p>
 *
 * @author 
 * @since 2025-06-28
 */
public interface IUserChatService extends IService<UserChat> {

    /**
     * 判断该用户是不是与该chatId有关
     * @param chatId
     * @param userId
     * @return
     */
    boolean isUserConnectChat(String chatId, long userId);

    List<String> getChatHistoryByUserId(long userId);

    boolean deleteChat(String chatId, long userId);
}
