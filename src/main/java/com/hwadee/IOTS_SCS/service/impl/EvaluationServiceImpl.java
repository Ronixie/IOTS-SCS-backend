package com.hwadee.IOTS_SCS.service.impl;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.service.EvaluationService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
* @ProjectName: IOTS-SCS-backend
* @Title: EvaluationServiceImpl
* @Package: com.hwadee.IOTS_SCS.service.impl
* @Description: 学习效果评价服务类的具体实现
* @author qiershi
* @date 2025/7/2 8:18
* @version V1.0
* Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
*/
@Service
public class EvaluationServiceImpl implements EvaluationService {

    @Override
    public CommonResult<Map> getReport(String uid, String period) {
        return CommonResult.success(Map.of("uid", uid, "period", period));
    }

}