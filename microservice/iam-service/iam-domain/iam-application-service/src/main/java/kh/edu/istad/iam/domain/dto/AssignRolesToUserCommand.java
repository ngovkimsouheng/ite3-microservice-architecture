package kh.edu.istad.iam.domain.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AssignRolesToUserCommand(
            UUID userUuid,
            List<UUID> roleUuids,
            OffsetDateTime expiresAt,
            String reason
    ) {}
