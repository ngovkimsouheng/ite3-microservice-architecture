package kh.edu.istad.iam.application.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommonDtos {

    // --- User Commands & Results ---

    public record UserResult(
            UUID uuid,
            String username,
            String email,
            boolean emailVerified,
            String givenName,
            String familyName,
            String locale,
            boolean enabled,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}

    public record UserSummary(
            UUID uuid,
            String username,
            String email,
            String givenName,
            String familyName,
            boolean enabled
    ) {}

    public record PageResult<T>(
            List<T> content,
            int pageNumber,
            int pageSize,
            long totalElements,
            int totalPages
    ) {}

    public record RegisterUserCommand(
            String username,
            String email,
            String password,
            String confirmPassword,
            String givenName,
            String familyName,
            String locale
    ) {}

    public record CreateUserCommand(
            String username,
            String email,
            String password,
            String givenName,
            String familyName,
            String gender,
            LocalDate birthdate,
            String phoneNumber,
            String zoneinfo,
            String locale,
            String addressStreetAddress,
            String addressLocality,
            String addressRegion,
            String addressPostalCode,
            String addressCountry,
            List<UUID> roleUuids
    ) {}

    public record UpdateUserProfileCommand(
            UUID userUuid,
            String givenName,
            String nickname,
            String phoneNumber,
            String locale
    ) {}

    public record ChangePasswordCommand(
            String currentPassword,
            String newPassword,
            String confirmPassword
    ) {}

    public record ResetUserPasswordCommand(
            UUID userUuid,
            String newPassword
    ) {}

    public record AssignRolesToUserCommand(
            UUID userUuid,
            List<UUID> roleUuids,
            OffsetDateTime expiresAt,
            String reason
    ) {}

    public record RoleAssignmentResult(
            UUID roleUuid,
            String roleName,
            OffsetDateTime assignedAt,
            OffsetDateTime expiresAt
    ) {}

    public record GrantPermissionsToUserCommand(
            UUID userUuid,
            List<UUID> permissionUuids,
            String effect, // GRANT or DENY
            OffsetDateTime expiresAt,
            String reason
    ) {}

    public record UserRoleResult(
            UUID uuid,
            String name
    ) {}

    public record PermissionGrantResult(
            UUID permissionUuid,
            String permissionName,
            String effect,
            OffsetDateTime assignedAt
    ) {}

    public record UserAuthoritiesResult(
            UUID userUuid,
            Set<String> roles,
            Set<String> permissions,
            List<UserRoleResult> roleDetails
    ) {}

    public record AuthUserResult(
            UUID uuid,
            String username,
            String email,
            String passwordHash,
            boolean enabled,
            boolean accountNonExpired,
            boolean accountNonLocked,
            boolean credentialsNonExpired,
            Set<String> authorities
    ) {}

    // --- Role Commands & Results ---

    public record CreateRoleCommand(String name, String description) {}
    public record UpdateRoleCommand(UUID roleUuid, String name, String description) {}
    public record RoleResult(UUID uuid, String name, String description, boolean isSystem, boolean active) {}
    public record RoleSummary(UUID uuid, String name, boolean active) {}
    public record AssignPermissionsToRoleCommand(UUID roleUuid, List<UUID> permissionUuids, String reason) {}
    public record PermissionAssignmentResult(UUID permissionUuid, String permissionName, OffsetDateTime assignedAt) {}

    // --- Permission Commands & Results ---

    public record CreatePermissionCommand(String resource, String action, String description) {}
    public record UpdatePermissionCommand(UUID permissionUuid, String description) {}
    public record PermissionResult(
            UUID uuid, String name, String resource, String action, String description, boolean isSystem, boolean active
    ) {}

    // --- Identity Commands & Results ---

    public record FederatedIdentityCommand(
            String provider,
            String providerSubject,
            String email,
            boolean emailVerified,
            String displayName,
            String username,
            String pictureUrl,
            Map<String, Object> attributes
    ) {}

    public record IdentityResult(
            UUID uuid,
            String provider,
            String providerSubject,
            String email,
            String displayName,
            OffsetDateTime linkedAt
    ) {}
}