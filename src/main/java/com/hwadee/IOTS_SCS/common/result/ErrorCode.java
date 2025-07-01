package com.hwadee.IOTS_SCS.common.result;

import lombok.Data;

/**
 * @author qiershi
 * @version V1.0
 * Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
 * @ProjectName:smart_study
 * @Title: ErrorCode
 * @Package com.csu.smartstudy.result
 * @Description: 错误码
 * @date 2025/6/29 9:07
 */
@Data
public class ErrorCode {

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String msg;

    public ErrorCode(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}