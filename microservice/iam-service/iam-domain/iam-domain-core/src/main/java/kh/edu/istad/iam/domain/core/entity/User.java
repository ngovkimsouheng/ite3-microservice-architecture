package kh.edu.istad.iam.domain.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private UUID uuid;
    private String username;
    private String email;
    private boolean emailVerified;
    private String passwordHash;
    private String givenName;
    private String familyName;
    private String middleName;
    private String nickname;
    private String gender;
    private LocalDate birthdate;
    private String profileUrl;
    private String pictureUrl;
    private String websiteUrl;
    private String zoneinfo;
    private String locale;
    private String phoneNumber;
    private boolean phoneNumberVerified;
    private String addressStreetAddress;
    private String addressLocality;
    private String addressRegion;
    private String addressPostalCode;
    private String addressCountry;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private int failedLoginAttempts;
    private OffsetDateTime lastLoginAt;
    private OffsetDateTime passwordChangedAt;
    private boolean isDeleted;
    private OffsetDateTime deletedAt;
    private String deletedBy;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
    private Long version;

    /**
     * Rule B.6:
     * Effective permissions = active role permissions + direct GRANT - direct DENY.
     */
    public Set<String> calculateEffectivePermissions(
            Set<String> activeRolePermissions,
            List<UserPermissionAssignment> directAssignments,
            Map<Long, String> permissionNameLookup
    ) {
        Set<String> effective = new HashSet<>(activeRolePermissions);

        if (directAssignments == null) {
            return effective;
        }

        // Add direct GRANTs
        for (UserPermissionAssignment assignment : directAssignments) {
            if (assignment.isActive() && assignment.isGrant()) {
                String permName = permissionNameLookup.get(assignment.getPermissionId());
                if (permName != null) {
                    effective.add(permName);
                }
            }
        }

        // Remove direct DENYs
        for (UserPermissionAssignment assignment : directAssignments) {
            if (assignment.isActive() && assignment.isDeny()) {
                String permName = permissionNameLookup.get(assignment.getPermissionId());
                if (permName != null) {
                    effective.remove(permName);
                }
            }
        }

        return effective;
    }

    /**
     * Rule B.6:
     * One identity per provider per user; a user cannot unlink their last sign-in method.
     */
    public void validateCanUnlinkIdentity(List<Identity> activeIdentities, UUID identityUuidToUnlink) {
        boolean hasPassword = this.passwordHash != null && !this.passwordHash.isBlank();
        long activeSocialCount = activeIdentities.stream()
                .filter(i -> !i.isDeleted())
                .count();

        // If no password exists and only 1 social account is linked, unlinking is forbidden
        if (!hasPassword && activeSocialCount <= 1) {
            throw new IllegalStateException("Cannot unlink the last remaining sign-in method.");
        }
    }
}