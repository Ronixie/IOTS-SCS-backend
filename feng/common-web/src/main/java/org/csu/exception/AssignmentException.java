package org.csu.exception;

public class AssignmentException extends CommonException {

    public AssignmentException(String message) {
        super(message, 200);
    }

    public AssignmentException(String message, Throwable cause) {
        super(message, cause, 200);
    }

    public AssignmentException(Throwable cause) {
        super(cause, 200);
    }
}
