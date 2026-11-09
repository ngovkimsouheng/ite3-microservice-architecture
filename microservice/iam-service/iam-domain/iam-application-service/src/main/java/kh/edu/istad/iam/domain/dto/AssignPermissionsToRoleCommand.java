package kh.edu.istad.iam.domain.dto;

import java.util.List;
import java.util.UUID;

public record AssignPermissionsToRoleCommand(UUID roleUuid, List<UUID> permissionUuids, String reason) {}
