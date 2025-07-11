package org.csu.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.csu.ai.entity.po.UserChat;

/**
 * <p>
 * 用户id与AIchatId对应表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-06-28
 */
@Mapper
public interface UserChatMapper extends BaseMapper<UserChat> {

}
