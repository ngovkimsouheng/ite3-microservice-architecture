package kh.edu.istad.iam.domain.dto;

import java.util.UUID;

public record PermissionResult(
            UUID uuid, String name, String resource, String action, String description, boolean isSystem, boolean active
    ) {}
