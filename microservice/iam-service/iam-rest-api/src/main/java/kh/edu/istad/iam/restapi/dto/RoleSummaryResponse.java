package kh.edu.istad.iam.restapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record RoleSummaryResponse(
        UUID uuid,
        String name,
        String description,
        @JsonProperty("isSystem")
        boolean isSystem,
        @JsonProperty("isActive")
        boolean isActive
) {
    public boolean active() {
        return isActive;
    }

    public boolean system() {
        return isSystem;
    }
}
