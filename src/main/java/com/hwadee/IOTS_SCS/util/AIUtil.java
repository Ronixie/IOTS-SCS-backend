package com.hwadee.IOTS_SCS.util;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: AIUtil
* @Package: com.hwadee.IOTS_SCS.util
* @Description: 连接AI的工具类
* @author qiershi
* @date 2025/7/5 15:31
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Component
public class AIUtil {

    private static final String API_KEY = "sk-ee6ee1f9e4984a48a9e763175a877694";
    private static final Integer SEED = 20250705;

    public static String callWithMessage(String data) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant. ")
                .build();

        String msg = "";
        if(data != null) msg =  "请帮我分析下面的学生数据：" + data + "。给出对此学生的建议。";
        else return "无数据";

        // msg = "你是谁";

        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(msg)
                .build();

        Float tem = 0.1F;

        GenerationParam param = GenerationParam.builder()
                .apiKey(API_KEY)
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .temperature(tem)
                .seed(SEED)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        return JsonUtils.toJson(gen.call(param));
    }

    public static void main(String[] args) {
        try {
            String result = AIUtil.callWithMessage("");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("An error occurred while generating the result: " + e.getMessage());
        }
    }

}