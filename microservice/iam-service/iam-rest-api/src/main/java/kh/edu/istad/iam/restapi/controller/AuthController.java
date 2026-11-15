package kh.edu.istad.iam.restapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.dto.SelfServiceResetPasswordCommand;
import kh.edu.istad.iam.domain.dto.UserResult;
import kh.edu.istad.iam.domain.usecase.security.RecordSecurityEventUseCase;
import kh.edu.istad.iam.domain.usecase.user.RegisterUserUseCase;
import kh.edu.istad.iam.domain.usecase.user.SelfServiceResetPasswordUseCase;
import kh.edu.istad.iam.restapi.dto.RegisterUserRequest;
import kh.edu.istad.iam.restapi.dto.SelfServiceResetPasswordRequest;
import kh.edu.istad.iam.restapi.dto.UserResponse;
import kh.edu.istad.iam.restapi.mapper.IamWebMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final SelfServiceResetPasswordUseCase selfServiceResetPasswordUseCase;
    private final RecordSecurityEventUseCase recordSecurityEventUseCase;
    private final IamWebMapper webMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest req) {
        UserResult result = registerUserUseCase.execute(webMapper.toCommand(req));
        UserResponse response = webMapper.toUserResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody SelfServiceResetPasswordRequest req,
            HttpServletRequest servletRequest
    ) {
        User user = selfServiceResetPasswordUseCase.execute(
                new SelfServiceResetPasswordCommand(
                        req.username(),
                        req.email(),
                        req.newPassword(),
                        req.confirmPassword()
                )
        );

        try {
            String ip = extractClientIp(servletRequest);
            recordSecurityEventUseCase.execute(
                    user.getUuid(),
                    user.getUsername(),
                    "ACTIVITY",
                    "PASSWORD_RESET",
                    "SUCCESS",
                    ip,
                    null,
                    "Phnom Penh, Cambodia",
                    null,
                    "Password Reset",
                    "Account password was reset via verified email"
            );
        } catch (Exception ex) {
            log.error("Failed to record password reset security event: {}", ex.getMessage());
        }

        return ResponseEntity.ok(Map.of(
                "message", "Password has been successfully reset. You may now sign in with your new password.",
                "username", user.getUsername()
        ));
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1 (Localhost)";
        String ip = null;
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank() && !"unknown".equalsIgnoreCase(xf)) {
            ip = xf.split(",")[0].trim();
        }
        if (ip == null || ip.isBlank()) {
            String xr = request.getHeader("X-Real-IP");
            if (xr != null && !xr.isBlank() && !"unknown".equalsIgnoreCase(xr)) {
                ip = xr.trim();
            }
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1") || ip.equals("127.0.0.1")) {
            return "127.0.0.1 (Localhost)";
        }
        return ip;
    }
}
