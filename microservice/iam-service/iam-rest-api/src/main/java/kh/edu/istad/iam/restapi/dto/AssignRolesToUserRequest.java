package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AssignRolesToUserRequest(
            @NotNull List<UUID> roleUuids,
            OffsetDateTime expiresAt,
            String reason
    ) {}
