package kh.edu.istad.iam.domain.dto;

import java.util.UUID;

public record RoleSummary(
        UUID uuid,
        String name,
        String description,
        boolean isSystem,
        boolean isActive
) {
    public boolean active() {
        return isActive;
    }
}
