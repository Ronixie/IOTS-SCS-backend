package org.csu.exception;


/**
 * 与考试服务相关异常
 */
public class ExamException extends CommonException {

    public ExamException(Throwable cause) {
        super(cause, 200);
    }

    public ExamException(String message, Throwable cause) {
        super(message, cause, 200);
    }

    public ExamException(String message) {
        super(message, 200);
    }
}
