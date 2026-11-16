package kh.edu.istad.iam.restapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID uuid,
        String username,
        String email,
        boolean emailVerified,
        String name,
        String givenName,
        String familyName,
        String nickname,
        String locale,
        String phoneNumber,
        boolean enabled,
        boolean accountNonLocked,
        List<RoleDto> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    @JsonProperty("accountLocked")
    public boolean accountLocked() {
        return !accountNonLocked;
    }
}
