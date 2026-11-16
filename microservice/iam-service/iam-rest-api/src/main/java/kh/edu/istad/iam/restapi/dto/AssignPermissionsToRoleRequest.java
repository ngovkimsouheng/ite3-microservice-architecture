package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record AssignPermissionsToRoleRequest(
            @NotNull(message = "permissionUuids cannot be null")
            List<UUID> permissionUuids,
            String reason
    ) {}
