package org.csu.advice;

import lombok.extern.slf4j.Slf4j;
import org.csu.exception.CommonException;
import org.csu.utils.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * 全局异常处理类，用于捕获和处理应用程序中的异常
 */
@RestControllerAdvice
@Slf4j
public class CommonExceptionAdvice {

    /**
     * 处理自定义异常
     * @param e 自定义异常对象
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(CommonException.class)
    public Object handleBadRequestException(CommonException e) {
        log.error("自定义异常 -> {} , 异常原因：{}  ",e.getClass().getName(), e.getMessage());
        log.debug("", e);
        return processResponse(e);
    }

    /**
     * 处理其他未捕获的异常
     * @param e 异常对象
     * @return 包含服务器内部错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public Object handleRuntimeException(Exception e) {
        log.error("其他异常 uri : {} -> ", ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getRequestURI(), e);
        return processResponse(new CommonException("服务器内部异常", 500));
    }

    /**
     * 构建异常响应
     * @param e 自定义异常对象
     * @return 包含错误信息的ResponseEntity
     */
    private ResponseEntity<Result<Void>> processResponse(CommonException e){
        return ResponseEntity.status(e.getCode()).body(Result.error(e.getMessage()));
    }
}