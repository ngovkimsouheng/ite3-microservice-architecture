package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank String newPassword,
            @NotBlank String confirmPassword
    ) {}
