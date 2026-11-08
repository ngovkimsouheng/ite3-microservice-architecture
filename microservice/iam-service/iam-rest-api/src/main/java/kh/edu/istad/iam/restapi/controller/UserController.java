package kh.edu.istad.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.iam.application.dto.CommonDtos.*;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.iam.application.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.iam.application.usecase.user.*;
import kh.edu.istad.iam.restapi.dto.RestDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final EnableUserUseCase enableUserUseCase;
    private final DisableUserUseCase disableUserUseCase;
    private final LockUserUseCase lockUserUseCase;
    private final UnlockUserUseCase unlockUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ResetUserPasswordUseCase resetUserPasswordUseCase;
    private final AssignRolesToUserUseCase assignRolesToUserUseCase;
    private final RevokeRoleFromUserUseCase revokeRoleFromUserUseCase;
    private final GrantPermissionsToUserUseCase grantPermissionsToUserUseCase;
    private final RevokePermissionFromUserUseCase revokePermissionFromUserUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;
    private final CurrentActorProvider currentActorProvider;

    // --- /api/users CRUD ---

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        AddressDto address = request.address();
        CreateUserCommand command = new CreateUserCommand(
                request.username(),
                request.email(),
                request.password(),
                request.givenName(),
                request.familyName(),
                request.gender(),
                request.birthdate(),
                request.phoneNumber(),
                request.zoneinfo(),
                request.locale(),
                address != null ? address.streetAddress() : null,
                address != null ? address.locality() : null,
                address != null ? address.region() : null,
                address != null ? address.postalCode() : null,
                address != null ? address.country() : null,
                request.roleUuids()
        );

        UserResult result = createUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<PageResult<UserSummary>> listUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        PageResult<UserSummary> result = listUsersUseCase.execute(page, size, sort);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/{uuid}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserResponse> getUserByUuid(@PathVariable("uuid") UUID uuid) {
        UserResult result = getUserUseCase.execute(uuid);
        return ResponseEntity.ok(toResponse(result));
    }

    @PatchMapping("/users/{uuid}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<UserResponse> updateUserProfile(
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        UserResult result = updateUserProfileUseCase.execute(new UpdateUserProfileCommand(
                uuid,
                request.username(),
                request.givenName(),
                request.nickname(),
                request.phoneNumber(),
                request.locale()
        ));
        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/users/{uuid}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> deleteUser(@PathVariable("uuid") UUID uuid) {
        deleteUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    // --- User Status & Password ---

    @PostMapping("/users/{uuid}/enable")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> enableUser(@PathVariable("uuid") UUID uuid) {
        enableUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{uuid}/disable")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> disableUser(@PathVariable("uuid") UUID uuid) {
        disableUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{uuid}/lock")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> lockUser(@PathVariable("uuid") UUID uuid) {
        lockUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{uuid}/unlock")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> unlockUser(@PathVariable("uuid") UUID uuid) {
        unlockUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{uuid}/password")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> resetPassword(
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody ResetUserPasswordRequest request
    ) {
        resetUserPasswordUseCase.execute(new ResetUserPasswordCommand(uuid, request.newPassword()));
        return ResponseEntity.noContent().build();
    }

    // --- Authorities & Assignments ---

    @GetMapping("/users/{uuid}/authorities")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserAuthoritiesResponse> getUserAuthorities(@PathVariable("uuid") UUID uuid) {
        UserAuthoritiesResult result = getUserAuthoritiesUseCase.execute(uuid);
        return ResponseEntity.ok(new UserAuthoritiesResponse(result.roles(), result.permissions()));
    }

    @PostMapping("/users/{uuid}/roles")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<List<RoleAssignmentResult>> assignRoles(
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody AssignRolesToUserRequest request
    ) {
        List<RoleAssignmentResult> results = assignRolesToUserUseCase.execute(
                new AssignRolesToUserCommand(uuid, request.roleUuids(), request.expiresAt(), request.reason())
        );
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/users/{uuid}/roles/{roleUuid}")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<Void> revokeRole(
            @PathVariable("uuid") UUID uuid,
            @PathVariable("roleUuid") UUID roleUuid,
            @RequestParam(required = false, defaultValue = "Revoked by admin") String reason
    ) {
        revokeRoleFromUserUseCase.execute(uuid, roleUuid, reason);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{uuid}/permissions")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<List<PermissionGrantResult>> grantPermissions(
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody GrantPermissionsToUserRequest request
    ) {
        List<PermissionGrantResult> results = grantPermissionsToUserUseCase.execute(
                new GrantPermissionsToUserCommand(uuid, request.permissionUuids(), request.effect(), request.expiresAt(), request.reason())
        );
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/users/{uuid}/permissions/{permissionUuid}")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<Void> revokePermission(
            @PathVariable("uuid") UUID uuid,
            @PathVariable("permissionUuid") UUID permissionUuid,
            @RequestParam(required = false, defaultValue = "Revoked by admin") String reason
    ) {
        revokePermissionFromUserUseCase.execute(uuid, permissionUuid, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{uuid}/identities")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<IdentityResponse>> listIdentities(@PathVariable("uuid") UUID uuid) {
        List<IdentityResult> results = listUserIdentitiesUseCase.execute(uuid);
        return ResponseEntity.ok(results.stream().map(this::toIdentityResponse).toList());
    }

    @DeleteMapping("/users/{uuid}/identities/{identityUuid}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> unlinkIdentity(
            @PathVariable("uuid") UUID uuid,
            @PathVariable("identityUuid") UUID identityUuid
    ) {
        unlinkIdentityUseCase.execute(uuid, identityUuid);
        return ResponseEntity.noContent().build();
    }

    // --- /api/me Endpoints ---

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
        return ResponseEntity.ok(toResponse(getUserUseCase.execute(currentUuid)));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserProfileRequest request) {
        UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
        UserResult result = updateUserProfileUseCase.execute(new UpdateUserProfileCommand(
                currentUuid,
                request.username(),
                request.givenName(),
                request.nickname(),
                request.phoneNumber(),
                request.locale()
        ));
        return ResponseEntity.ok(toResponse(result));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changeCurrentPassword(@Valid @RequestBody ChangePasswordRequest request) {
        UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
        changePasswordUseCase.execute(
                currentUuid,
                new ChangePasswordCommand(
                        request.currentPassword(),
                        request.newPassword(),
                        request.confirmPassword()
                )
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/identities")
    public ResponseEntity<List<IdentityResponse>> getCurrentUserIdentities() {
        UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
        List<IdentityResult> results = listUserIdentitiesUseCase.execute(currentUuid);
        return ResponseEntity.ok(results.stream().map(this::toIdentityResponse).toList());
    }

    @DeleteMapping("/me/identities/{identityUuid}")
    public ResponseEntity<Void> unlinkCurrentUserIdentity(@PathVariable("identityUuid") UUID identityUuid) {
        UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
        unlinkIdentityUseCase.execute(currentUuid, identityUuid);
        return ResponseEntity.noContent().build();
    }

    // --- Helpers ---

    private UserResponse toResponse(UserResult result) {
        UserAuthoritiesResult authorities = getUserAuthoritiesUseCase.execute(result.uuid());
        return new UserResponse(
                result.uuid(),
                result.username(),
                result.email(),
                result.emailVerified(),
                result.givenName() + " " + result.familyName(),
                result.locale(),
                result.phoneNumber(),
                result.enabled(),
                authorities.roleDetails() != null ? authorities.roleDetails().stream()
                        .map(role -> new RoleDto(role.uuid(), role.name()))
                        .toList() : List.of(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    private IdentityResponse toIdentityResponse(IdentityResult r) {
        return new IdentityResponse(
                r.uuid(),
                r.provider(),
                r.email(),
                r.displayName(),
                r.linkedAt()
        );
    }
}