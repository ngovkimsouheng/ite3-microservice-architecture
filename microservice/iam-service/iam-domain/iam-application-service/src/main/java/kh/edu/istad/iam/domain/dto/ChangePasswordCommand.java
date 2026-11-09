package kh.edu.istad.iam.domain.dto;

public record ChangePasswordCommand(
            String currentPassword,
            String newPassword,
            String confirmPassword
    ) {}
