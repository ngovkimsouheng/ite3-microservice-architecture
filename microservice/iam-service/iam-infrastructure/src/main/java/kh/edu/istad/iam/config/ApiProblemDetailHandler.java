package kh.edu.istad.iam.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiProblemDetailHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException, ServletException {
        writeProblem(response, HttpStatus.UNAUTHORIZED.value(), "Authentication required",
                "A valid bearer access token is required.", "https://iam.istad.co/problems/unauthorized");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException, ServletException {
        writeProblem(response, HttpStatus.FORBIDDEN.value(), "Access denied",
                "You do not have the required permission.", "https://iam.istad.co/problems/access-denied");
    }

    private void writeProblem(HttpServletResponse response, int status, String title,
                              String detail, String type) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter().printf(
                "{\"type\":\"%s\",\"title\":\"%s\",\"status\":%d,\"detail\":\"%s\"}",
                type, title, status, detail
        );
    }
}
