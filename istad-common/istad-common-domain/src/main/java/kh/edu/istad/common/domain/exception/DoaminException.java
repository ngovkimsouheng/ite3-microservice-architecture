package kh.edu.istad.common.domain.exception;

public class DoaminException extends RuntimeException{

    public DoaminException(String message) {
        super(message);
    }

    public DoaminException(String message, Throwable cause) {
        super(message, cause);
    }
}
