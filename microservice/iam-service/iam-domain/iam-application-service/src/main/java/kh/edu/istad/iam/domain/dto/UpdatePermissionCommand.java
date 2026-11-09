package kh.edu.istad.iam.domain.dto;

import java.util.UUID;

public record UpdatePermissionCommand(UUID permissionUuid, String description) {}
