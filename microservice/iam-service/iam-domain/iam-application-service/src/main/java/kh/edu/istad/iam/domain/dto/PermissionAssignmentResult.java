package kh.edu.istad.iam.domain.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PermissionAssignmentResult(UUID permissionUuid, String permissionName, OffsetDateTime assignedAt) {}
