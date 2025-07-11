package org.csu.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.csu.ai.entity.po.UserChat;
import org.csu.ai.mapper.UserChatMapper;
import org.csu.ai.service.IUserChatService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户id与AIchatId对应表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-06-28
 */
@Service
public class UserChatServiceImpl extends ServiceImpl<UserChatMapper, UserChat> implements IUserChatService {

    @Override
    public boolean isUserConnectChat(String chatId, long userId) {
        LambdaQueryWrapper<UserChat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserChat::getChatId, chatId);
        wrapper.eq(UserChat::getUserId, userId);
        return !list(wrapper).isEmpty();
    }

    @Override
    public List<String> getChatHistoryByUserId(long userId) {
        LambdaQueryWrapper<UserChat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserChat::getUserId, userId);
        return list(wrapper).stream().map(UserChat::getChatId).toList();
    }

    @Override
    public boolean deleteChat(String chatId, long userId) {
        LambdaQueryWrapper<UserChat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserChat::getChatId, chatId);
        wrapper.eq(UserChat::getUserId, userId);
        return remove(wrapper);
    }
}
