package kh.edu.istad.iam.domain.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserSummary(
        UUID uuid,
        String username,
        String email,
        String givenName,
        String familyName,
        String phoneNumber,
        String locale,
        boolean enabled,
        boolean accountNonLocked,
        List<String> roles,
        OffsetDateTime createdAt
) {
    public boolean accountLocked() {
        return !accountNonLocked;
    }

    public String name() {
        String gn = givenName != null ? givenName : "";
        String fn = familyName != null ? familyName : "";
        String full = (gn + " " + fn).trim();
        return full.isEmpty() ? username : full;
    }
}
