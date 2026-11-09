package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record AssignPermissionsToRoleRequest(
            @NotEmpty(message = "permissionUuids cannot be empty")
            List<UUID> permissionUuids,
            String reason
    ) {}
