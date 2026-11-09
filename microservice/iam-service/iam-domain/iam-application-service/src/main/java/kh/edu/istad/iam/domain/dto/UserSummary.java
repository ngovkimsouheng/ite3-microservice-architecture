package kh.edu.istad.iam.domain.dto;

import java.util.UUID;

public record UserSummary(
            UUID uuid,
            String username,
            String email,
            String givenName,
            String familyName,
            boolean enabled
    ) {}
