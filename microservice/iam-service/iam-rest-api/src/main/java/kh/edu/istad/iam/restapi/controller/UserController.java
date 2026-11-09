    package kh.edu.istad.iam.restapi.controller;

    import jakarta.validation.Valid;
    import kh.edu.istad.iam.domain.dto.*;
    import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
    import kh.edu.istad.iam.domain.usecase.identity.ListUserIdentitiesUseCase;
    import kh.edu.istad.iam.domain.usecase.identity.UnlinkIdentityUseCase;
    import kh.edu.istad.iam.domain.usecase.user.*;
    import kh.edu.istad.iam.restapi.dto.*;
    import kh.edu.istad.iam.restapi.mapper.IamWebMapper;
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
        private final IamWebMapper webMapper;

        // --- /api/users CRUD ---

        @PostMapping("/users")
        @PreAuthorize("hasAuthority('user:create')")
        public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
            UserResult result = createUserUseCase.execute(webMapper.toCommand(request));
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
            UserResult result = updateUserProfileUseCase.execute(webMapper.toCommand(uuid, request));
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
            resetUserPasswordUseCase.execute(webMapper.toCommand(uuid, request));
            return ResponseEntity.noContent().build();
        }

        // --- Authorities & Assignments ---

        @GetMapping("/users/{uuid}/authorities")
        @PreAuthorize("hasAuthority('user:read')")
        public ResponseEntity<UserAuthoritiesResponse> getUserAuthorities(@PathVariable("uuid") UUID uuid) {
            UserAuthoritiesResult result = getUserAuthoritiesUseCase.execute(uuid);
            return ResponseEntity.ok(webMapper.toAuthoritiesResponse(result));
        }

        @PostMapping("/users/{uuid}/roles")
        @PreAuthorize("hasAuthority('user:assign')")
        public ResponseEntity<List<RoleAssignmentResult>> assignRoles(
                @PathVariable("uuid") UUID uuid,
                @Valid @RequestBody AssignRolesToUserRequest request
        ) {
            List<RoleAssignmentResult> results =
                    assignRolesToUserUseCase.execute(webMapper.toCommand(uuid, request));
            return ResponseEntity.ok(results);
        }

        @DeleteMapping("/users/{uuid}/roles/{roleUuid}")
        @PreAuthorize("hasAuthority('user:assign')")
        public ResponseEntity<Void> revokeRole(
                @PathVariable("uuid") UUID uuid,
                @PathVariable("roleUuid") UUID roleUuid,
                @RequestParam(name = "reason", required = false) String reason        ) {
            revokeRoleFromUserUseCase.execute(uuid, roleUuid, reason);
            return ResponseEntity.noContent().build();
        }

        @PostMapping("/users/{uuid}/permissions")
        @PreAuthorize("hasAuthority('user:assign')")
        public ResponseEntity<List<PermissionGrantResult>> grantPermissions(
                @PathVariable("uuid") UUID uuid,
                @Valid @RequestBody GrantPermissionsToUserRequest request
        ) {
            List<PermissionGrantResult> results =
                    grantPermissionsToUserUseCase.execute(webMapper.toCommand(uuid, request));
            return ResponseEntity.ok(results);
        }

        @DeleteMapping("/users/{uuid}/permissions/{permissionUuid}")
        @PreAuthorize("hasAuthority('user:assign')")
        public ResponseEntity<Void> revokePermission(
                @PathVariable("uuid") UUID uuid,
                @PathVariable("permissionUuid") UUID permissionUuid,
                @RequestParam(name = "reason", required = false, defaultValue = "Revoked by admin") String reason
        ) {
            revokePermissionFromUserUseCase.execute(uuid, permissionUuid, reason);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/users/{uuid}/identities")
        @PreAuthorize("hasAuthority('user:read')")
        public ResponseEntity<List<IdentityResponse>> listIdentities(@PathVariable("uuid") UUID uuid) {
            List<IdentityResult> results = listUserIdentitiesUseCase.execute(uuid);
            return ResponseEntity.ok(results.stream().map(webMapper::toIdentityResponse).toList());
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
            UserResult result = updateUserProfileUseCase.execute(webMapper.toCommand(currentUuid, request));
            return ResponseEntity.ok(toResponse(result));
        }

        @PutMapping("/me/password")
        public ResponseEntity<Void> changeCurrentPassword(@Valid @RequestBody ChangePasswordRequest request) {
            UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
            changePasswordUseCase.execute(currentUuid, webMapper.toCommand(request));
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/me/identities")
        public ResponseEntity<List<IdentityResponse>> getCurrentUserIdentities() {
            UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
            List<IdentityResult> results = listUserIdentitiesUseCase.execute(currentUuid);
            return ResponseEntity.ok(results.stream().map(webMapper::toIdentityResponse).toList());
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
            return webMapper.toUserResponse(result, authorities);
        }
    }