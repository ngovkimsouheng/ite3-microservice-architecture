package kh.edu.istad.iam.domain.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResult(
            UUID uuid,
            String username,
            String email,
            boolean emailVerified,
            String givenName,
            String familyName,
            String locale,
            String phoneNumber,
            boolean enabled,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}
