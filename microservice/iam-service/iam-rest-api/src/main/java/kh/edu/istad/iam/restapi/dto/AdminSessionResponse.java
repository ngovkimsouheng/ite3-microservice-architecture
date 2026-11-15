package kh.edu.istad.iam.restapi.dto;

import java.time.Instant;

public record AdminSessionResponse(
        String sessionId,
        String username,
        String ipAddress,
        String device,
        Instant createdAt,
        Instant lastAccessedAt,
        boolean isCurrent
) {}
