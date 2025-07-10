package org.csu.exception;

public class AIException extends CommonException {
    public AIException(String message) {
        super(message, 200);
    }

    public AIException(String message, Throwable cause) {
        super(message, cause, 200);
    }

    public AIException(Throwable cause) {
        super(cause, 200);
    }
}
