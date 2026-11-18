package kh.edu.istad.iam.domain.dto;

public record SelfServiceResetPasswordCommand(
        String username,
        String email,
        String newPassword,
        String confirmPassword
) {}
