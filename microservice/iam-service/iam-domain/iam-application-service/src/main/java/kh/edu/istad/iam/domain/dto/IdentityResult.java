package kh.edu.istad.iam.domain.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IdentityResult(
            UUID uuid,
            String provider,
            String providerSubject,
            String email,
            String displayName,
            OffsetDateTime linkedAt
    ) {}
