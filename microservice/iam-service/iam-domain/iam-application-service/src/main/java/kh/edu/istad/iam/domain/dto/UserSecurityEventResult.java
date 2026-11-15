package kh.edu.istad.iam.domain.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserSecurityEventResult(
        UUID uuid,
        String username,
        String category,
        String eventType,
        String status,
        String ipAddress,
        String device,
        String location,
        String authMethod,
        String description,
        String details,
        OffsetDateTime createdAt
) {}
