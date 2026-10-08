package kh.edu.istad.platform.customer.domain.exception;

import kh.edu.istad.common.domain.exception.DoaminException;

public class CustomerDomainException extends DoaminException {

    public CustomerDomainException(String message) {
        super(message);
    }

    public CustomerDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
