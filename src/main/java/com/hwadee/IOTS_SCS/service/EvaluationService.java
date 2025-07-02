package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.common.result.CommonResult;

import java.util.Map;

public interface EvaluationService {
    CommonResult<Map> getReport(String uid, String period);
}
