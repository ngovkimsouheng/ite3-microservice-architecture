package kh.edu.istad.iam.restapi.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IdentityResponse(
            UUID uuid,
            String provider,
            String email,
            String displayName,
            OffsetDateTime linkedAt
    ) {}
