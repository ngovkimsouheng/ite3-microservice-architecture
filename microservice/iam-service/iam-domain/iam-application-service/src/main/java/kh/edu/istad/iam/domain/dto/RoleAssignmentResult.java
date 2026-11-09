package kh.edu.istad.iam.domain.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RoleAssignmentResult(
            UUID roleUuid,
            String roleName,
            OffsetDateTime assignedAt,
            OffsetDateTime expiresAt
    ) {}
