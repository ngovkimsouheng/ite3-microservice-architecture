package kh.edu.istad.iam.domain.dto;

import java.util.UUID;

public record UserRoleResult(
            UUID uuid,
            String name
    ) {}
