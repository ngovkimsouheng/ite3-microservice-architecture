package kh.edu.istad.iam.restapi.dto;

import java.time.OffsetDateTime;

public record LoginHistoryResponse(
        String id,
        String status,
        OffsetDateTime timestamp,
        String ipAddress,
        String device,
        String location,
        String method
) {}
