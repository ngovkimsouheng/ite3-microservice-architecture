package kh.edu.istad.iam.restapi.dto;

import java.util.UUID;

public record PermissionResponse(
            UUID uuid,
            String name,
            String resource,
            String action,
            String description,
            boolean isSystem,
            boolean isActive
    ) {}
