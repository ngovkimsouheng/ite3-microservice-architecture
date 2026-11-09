package kh.edu.istad.iam.restapi.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
            UUID uuid,
            String username,
            String email,
            boolean emailVerified,
            String name,
            String locale,
            String phoneNumber,
            boolean enabled,
            List<RoleDto> roles,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}
