package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kh.edu.istad.iam.application.dto.CommonDtos;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RestDtos {

    // Request DTOs (D.4)
    public record RegisterUserRequest(
            @NotBlank(message = "Username cannot be blank")
            @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
            String username,

            @NotBlank(message = "Email cannot be blank")
            @Email(message = "must be a well-formed email address")
            String email,

            @NotBlank(message = "Password cannot be blank")
            String password,

            @NotBlank(message = "Password confirmation cannot be blank")
            String confirmPassword,

            @NotBlank(message = "Given name cannot be blank")
            String givenName,

            @NotBlank(message = "Family name cannot be blank")
            String familyName,

            @NotBlank(message = "Locale cannot be blank")
            String locale
    ) {}

    public record CreateUserRequest(
            @NotBlank String username,
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotBlank String givenName,
            @NotBlank String familyName,
            String gender,
            LocalDate birthdate,
            String phoneNumber,
            String zoneinfo,
            @NotBlank String locale,
            AddressDto address,
            List<UUID> roleUuids
    ) {}

    public record AddressDto(
            String streetAddress,
            String locality,
            String region,
            String postalCode,
            String country
    ) {}

    public record UpdateUserProfileRequest(
            @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
            String username,
            String givenName,
            String nickname,
            @Size(max = 20, message = "Phone number must not exceed 20 characters")
            String phoneNumber,
            String locale
    ) {}

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank String newPassword,
            @NotBlank String confirmPassword
    ) {}

    public record ResetUserPasswordRequest(
            @NotBlank(message = "New password cannot be blank")
            String newPassword
    ) {}

    public record AssignRolesToUserRequest(
            @NotNull List<UUID> roleUuids,
            OffsetDateTime expiresAt,
            String reason
    ) {}

    public record GrantPermissionsToUserRequest(
            @NotNull List<UUID> permissionUuids,
            @NotBlank String effect,
            OffsetDateTime expiresAt,
            String reason
    ) {}

    public record CreateRoleRequest(
            @NotBlank String name,
            String description,
            List<UUID> permissionUuids
    ) {}

    public record UpdateRoleRequest(
            @NotBlank(message = "Name is required")
            @Size(max = 50, message = "Name must not exceed 50 characters")
            String name,

            @Size(max = 255, message = "Description must not exceed 255 characters")
            String description
    ) {}

    public record AssignPermissionsToRoleRequest(
            @NotEmpty(message = "permissionUuids cannot be empty")
            List<UUID> permissionUuids,
            String reason
    ) {}

    public record CreatePermissionRequest(
            @NotBlank String resource,
            @NotBlank String action,
            String description
    ) {}

    public record UpdatePermissionRequest(
            String description
    ) {}

    // Response DTOs (D.5)
    public record UserResponse(
            UUID uuid,
            String username,
            String email,
            boolean emailVerified,
            String name,
            String locale,
            String phoneNumber,
            boolean enabled,
            List<RoleDto> roles,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}

    public record RoleDto(
            UUID uuid,
            String name
    ) {}

    public record RoleSummaryResponse(
            UUID uuid,
            String name,
            boolean active
    ) {}

    public record RoleResponse(
            UUID uuid,
            String name,
            String description,
            boolean isSystem,
            boolean isActive
    ) {}

    public record PermissionResponse(
            UUID uuid,
            String name,
            String resource,
            String action,
            String description,
            boolean isSystem,
            boolean isActive
    ) {}

    public record UserAuthoritiesResponse(
            Set<String> roles,
            Set<String> effectivePermissions
    ) {}

    public record IdentityResponse(
            UUID uuid,
            String provider,
            String email,
            String displayName,
            OffsetDateTime linkedAt
    ) {}

    public record PageMetadata(
            int number,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public record PageResponse<T>(
            List<T> content,
            PageMetadata page
    ) {
        public static <T> PageResponse<T> from(CommonDtos.PageResult<T> result) {
            return new PageResponse<>(
                    result.content(),
                    new PageMetadata(
                            result.pageNumber(),
                            result.pageSize(),
                            result.totalElements(),
                            result.totalPages()
                    )
            );
        }
    }
}