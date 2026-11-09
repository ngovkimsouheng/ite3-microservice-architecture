package kh.edu.istad.iam.restapi.dto;

import java.util.UUID;

public record RoleResponse(
            UUID uuid,
            String name,
            String description,
            boolean isSystem,
            boolean isActive
    ) {}
