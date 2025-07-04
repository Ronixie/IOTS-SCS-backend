package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.EvaluationReportDTO;

import java.util.Map;

public interface EvaluationService {
    CommonResult<EvaluationReportDTO> generateReport(String period, String uid);
}
