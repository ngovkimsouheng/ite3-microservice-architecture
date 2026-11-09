package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest(
            @NotBlank String resource,
            @NotBlank String action,
            String description
    ) {}
