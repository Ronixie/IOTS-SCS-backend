package org.csu.exception;

import lombok.Getter;

/**
 * 自定义异常
 */
@Getter
public class CommonException extends RuntimeException{
    private final int code;

    public CommonException(String message, int code) {
        super(message);
        this.code = code;
    }

    public CommonException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public CommonException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }
}
