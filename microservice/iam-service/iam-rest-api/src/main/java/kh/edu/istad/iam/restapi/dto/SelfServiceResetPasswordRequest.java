package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SelfServiceResetPasswordRequest(
        @NotBlank(message = "Username or account identifier is required")
        String username,

        @NotBlank(message = "Registered email address is required")
        String email,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword,

        @NotBlank(message = "Password confirmation is required")
        String confirmPassword
) {}
