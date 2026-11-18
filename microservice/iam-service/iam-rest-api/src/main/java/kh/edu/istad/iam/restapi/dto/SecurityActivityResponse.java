package kh.edu.istad.iam.restapi.dto;

import java.time.OffsetDateTime;

public record SecurityActivityResponse(
        String id,
        String event,
        String description,
        OffsetDateTime timestamp,
        String icon,
        String color,
        String ipAddress,
        String eventType,
        String username
) {
    public SecurityActivityResponse(String id, String event, String description, OffsetDateTime timestamp, String icon, String color) {
        this(id, event, description, timestamp, icon, color, "127.0.0.1 (Localhost)", event, null);
    }
}
