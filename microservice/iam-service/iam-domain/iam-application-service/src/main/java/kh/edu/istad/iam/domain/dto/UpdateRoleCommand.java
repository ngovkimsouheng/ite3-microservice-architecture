package kh.edu.istad.iam.domain.dto;

import java.util.UUID;

public record UpdateRoleCommand(UUID roleUuid, String name, String description) {}
