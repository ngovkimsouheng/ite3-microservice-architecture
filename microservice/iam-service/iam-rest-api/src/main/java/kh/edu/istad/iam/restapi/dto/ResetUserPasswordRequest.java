package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetUserPasswordRequest(
            @NotBlank(message = "New password cannot be blank")
            String newPassword
    ) {}
