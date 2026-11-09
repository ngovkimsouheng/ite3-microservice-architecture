package kh.edu.istad.iam.restapi.dto;

import java.util.UUID;

public record RoleSummaryResponse(
            UUID uuid,
            String name,
            boolean active
    ) {}
