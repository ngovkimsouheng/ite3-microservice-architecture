package kh.edu.istad.iam.domain.dto;

import java.util.UUID;

public record RoleResult(UUID uuid, String name, String description, boolean isSystem, boolean active) {}
