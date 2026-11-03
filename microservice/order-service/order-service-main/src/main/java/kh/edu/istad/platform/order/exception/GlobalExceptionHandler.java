package kh.edu.istad.platform.order.exception;

import kh.edu.istad.platform.order.client.exception.DownStreamServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class  GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler (DownStreamServiceException.class)
    public ResponseEntity<?> handle (DownStreamServiceException ex){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "service", ex.getService(),
                                "message", "Down Stream Service Error",
                                "details", ex.getMessage()

                        )
                );
    }
}