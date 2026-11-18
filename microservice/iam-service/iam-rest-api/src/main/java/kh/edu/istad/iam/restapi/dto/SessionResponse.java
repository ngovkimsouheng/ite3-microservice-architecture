package kh.edu.istad.iam.restapi.dto;

import java.time.Instant;

public record SessionResponse(
        String sessionId,
        String ipAddress,
        String device,
        Instant createdAt,
        Instant lastAccessedAt,
        boolean isCurrent
) {}