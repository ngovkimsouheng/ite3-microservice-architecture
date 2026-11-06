package kh.edu.istad.iam.restapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record FieldErrorItem(String field, String message) {}

    /**
     * Handles Jakarta Bean Validation errors (HTTP 400).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("https://iam.istad.co/problems/validation-error"));
        problem.setTitle("Validation failed");

        List<FieldErrorItem> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new FieldErrorItem(err.getField(), err.getDefaultMessage()))
                .toList();

        problem.setProperty("errors", errors);
        return problem;
    }

    /**
     * Handles invalid arguments or business validation failures (HTTP 400).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("https://iam.istad.co/problems/bad-request"));
        problem.setTitle("Invalid Request");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    /**
     * Handles business conflicts and unmodifiable resource violations (HTTP 409).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(IllegalStateException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setType(URI.create("https://iam.istad.co/problems/conflict"));
        problem.setTitle("State Conflict");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    /**
     * Handles unauthorized access explicitly (HTTP 403).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setType(URI.create("https://iam.istad.co/problems/access-denied"));
        problem.setTitle("Access Denied");
        problem.setDetail("You do not have the required permissions to perform this action.");
        return problem;
    }

    /**
     * Re-throws Spring Security AuthenticationException so filter chain handles 401 challenges.
     */
    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(AuthenticationException ex) {
        throw ex;
    }

    /**
     * Fallback for uncaught server exceptions (HTTP 500) with logging.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unhandled server exception encountered: ", ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setType(URI.create("https://iam.istad.co/problems/internal-error"));
        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected error occurred. Please contact system support.");
        return problem;
    }
}