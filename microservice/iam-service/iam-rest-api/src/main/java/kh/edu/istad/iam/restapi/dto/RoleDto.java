package kh.edu.istad.iam.restapi.dto;

import java.util.UUID;

public record RoleDto(
            UUID uuid,
            String name
    ) {}
