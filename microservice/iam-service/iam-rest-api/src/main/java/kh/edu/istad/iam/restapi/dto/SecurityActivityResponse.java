package kh.edu.istad.iam.restapi.dto;

import java.time.OffsetDateTime;

public record SecurityActivityResponse(
        String id,
        String event,
        String description,
        OffsetDateTime timestamp,
        String icon,
        String color
) {}
