package kh.edu.istad.iam.domain.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record GrantPermissionsToUserCommand(
            UUID userUuid,
            List<UUID> permissionUuids,
            String effect, // GRANT or DENY
            OffsetDateTime expiresAt,
            String reason
    ) {}
