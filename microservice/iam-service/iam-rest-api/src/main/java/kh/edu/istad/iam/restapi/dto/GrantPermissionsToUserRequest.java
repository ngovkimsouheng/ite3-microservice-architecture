package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GrantPermissionsToUserRequest(
            @NotNull List<UUID> permissionUuids,
            @NotBlank String effect,
            OffsetDateTime expiresAt,
            String reason
    ) {}
