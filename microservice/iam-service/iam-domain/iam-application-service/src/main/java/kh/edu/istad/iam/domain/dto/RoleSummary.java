package kh.edu.istad.iam.domain.dto;

import java.util.UUID;

public record RoleSummary(UUID uuid, String name, boolean active) {}
