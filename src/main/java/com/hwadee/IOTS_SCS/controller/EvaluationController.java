package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.util.JwtUtil;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: EvaluationController
* @Package: com.hwadee.IOTS_SCS.controller
* @Description: 学习效果评价服务
* @author qiershi
* @date 2025/7/2 8:11
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping("/report")
    public CommonResult<Map<String, Object>> getReport(
            @PathParam("period") String period,
            @RequestHeader("Authorization") String token) {
        String uidFromToken = jwtUtil.getUidFromToken(token);
        return CommonResult.success();
    }

}