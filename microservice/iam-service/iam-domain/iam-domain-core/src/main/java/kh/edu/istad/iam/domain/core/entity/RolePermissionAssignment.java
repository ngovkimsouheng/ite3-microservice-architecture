package kh.edu.istad.iam.domain.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionAssignment {
    private Long id;
    private Long roleId;
    private Long permissionId;
    private OffsetDateTime assignedAt;
    private String assignedBy;
    private OffsetDateTime expiresAt;
    private OffsetDateTime revokedAt;
    private String revokedBy;
    private String reason;

    public boolean isActive() {
        if (revokedAt != null) {
            return false;
        }
        return expiresAt == null || expiresAt.isAfter(OffsetDateTime.now());
    }

    public void revoke(String revokedBy, String reason) {
        this.revokedAt = OffsetDateTime.now();
        this.revokedBy = revokedBy;
        this.reason = reason;
    }
}