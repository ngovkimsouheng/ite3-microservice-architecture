package kh.edu.istad.iam.domain.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PermissionGrantResult(
            UUID permissionUuid,
            String permissionName,
            String effect,
            OffsetDateTime assignedAt
    ) {}
