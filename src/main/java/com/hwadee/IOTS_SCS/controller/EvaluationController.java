package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.EvaluationReportDTO;
import com.hwadee.IOTS_SCS.service.EvaluationService;
import com.hwadee.IOTS_SCS.util.JwtUtil;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor()
public class EvaluationController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EvaluationService evaluationService;

    @GetMapping("/report")
    public CommonResult<EvaluationReportDTO> getReport(
            @PathParam("period") String period,
            @RequestHeader("Authorization") String token) {
        String uidFromToken = jwtUtil.getUidFromToken(token);
        return evaluationService.generateReport(period, uidFromToken);
    }

}