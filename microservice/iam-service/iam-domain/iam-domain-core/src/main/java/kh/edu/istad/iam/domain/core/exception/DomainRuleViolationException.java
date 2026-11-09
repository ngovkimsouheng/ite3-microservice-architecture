package kh.edu.istad.iam.domain.core.exception;

public class DomainRuleViolationException extends IllegalStateException {

    public DomainRuleViolationException(String message) {
        super(message);
    }
}
