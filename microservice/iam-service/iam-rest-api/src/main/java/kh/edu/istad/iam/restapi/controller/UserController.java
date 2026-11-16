    package kh.edu.istad.iam.restapi.controller;

    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.validation.Valid;
    import kh.edu.istad.iam.domain.dto.*;
    import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
    import kh.edu.istad.iam.domain.usecase.identity.ListUserIdentitiesUseCase;
    import kh.edu.istad.iam.domain.usecase.identity.UnlinkIdentityUseCase;
    import kh.edu.istad.iam.domain.usecase.security.ListLoginHistoryUseCase;
    import kh.edu.istad.iam.domain.usecase.security.ListSecurityActivityUseCase;
    import kh.edu.istad.iam.domain.usecase.security.RecordSecurityEventUseCase;
    import kh.edu.istad.iam.domain.usecase.user.*;
    import kh.edu.istad.iam.restapi.dto.*;
    import kh.edu.istad.iam.restapi.mapper.IamWebMapper;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.UUID;

    @Slf4j
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
        private final ListLoginHistoryUseCase listLoginHistoryUseCase;
        private final ListSecurityActivityUseCase listSecurityActivityUseCase;
        private final RecordSecurityEventUseCase recordSecurityEventUseCase;
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
        public ResponseEntity<Void> disableUser(@PathVariable("uuid") UUID uuid, HttpServletRequest servletRequest) {
            disableUserUseCase.execute(uuid);
            try {
                UserResult user = getUserUseCase.execute(uuid);
                recordSecurityEventUseCase.execute(
                        uuid,
                        user.username(),
                        "ACTIVITY",
                        "ACCOUNT_DEACTIVATED",
                        "SUCCESS",
                        extractClientIp(servletRequest),
                        servletRequest.getHeader("User-Agent"),
                        "Phnom Penh, Cambodia",
                        null,
                        "Account Disabled / Deactivated",
                        "Administrator disabled account access for @" + user.username()
                );
            } catch (Exception ignored) {}
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
        public ResponseEntity<UserResponse> updateCurrentUser(
                @Valid @RequestBody UpdateUserProfileRequest request,
                jakarta.servlet.http.HttpServletRequest servletRequest
        ) {
            UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
            UserResult result = updateUserProfileUseCase.execute(webMapper.toCommand(currentUuid, request));

            org.springframework.security.core.Authentication currentAuth =
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            String oldUsername = (currentAuth != null) ? currentAuth.getName() : null;

            // Sync updated username to Spring Security context and session if changed
            if (currentAuth != null && currentAuth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
                if (!userDetails.getUsername().equals(result.username())) {
                    org.springframework.security.core.userdetails.UserDetails updatedUser =
                            org.springframework.security.core.userdetails.User.builder()
                                    .username(result.username())
                                    .password(userDetails.getPassword() != null ? userDetails.getPassword() : "")
                                    .authorities(userDetails.getAuthorities())
                                    .accountExpired(!userDetails.isAccountNonExpired())
                                    .accountLocked(!userDetails.isAccountNonLocked())
                                    .credentialsExpired(!userDetails.isCredentialsNonExpired())
                                    .disabled(!userDetails.isEnabled())
                                    .build();

                    org.springframework.security.authentication.UsernamePasswordAuthenticationToken newAuth =
                            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                    updatedUser,
                                    currentAuth.getCredentials(),
                                    currentAuth.getAuthorities()
                            );
                    newAuth.setDetails(currentAuth.getDetails());
                    org.springframework.security.core.context.SecurityContext newContext =
                            org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
                    newContext.setAuthentication(newAuth);
                    org.springframework.security.core.context.SecurityContextHolder.setContext(newContext);

                    if (servletRequest.getSession(false) != null) {
                        servletRequest.getSession(false).setAttribute(
                                org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                                newContext
                        );
                    }
                }
            }

            // Record real security activity
            try {
                String ip = extractClientIp(servletRequest);
                if (oldUsername != null && !oldUsername.equals(result.username())) {
                    recordSecurityEventUseCase.execute(
                            currentUuid,
                            result.username(),
                            "ACTIVITY",
                            "USERNAME_CHANGED",
                            "SUCCESS",
                            ip,
                            null,
                            "Phnom Penh, Cambodia",
                            null,
                            "Username Changed",
                            "Changed username from @" + oldUsername + " to @" + result.username()
                    );
                } else {
                    recordSecurityEventUseCase.execute(
                            currentUuid,
                            result.username(),
                            "ACTIVITY",
                            "PROFILE_UPDATED",
                            "SUCCESS",
                            ip,
                            null,
                            "Phnom Penh, Cambodia",
                            null,
                            "Profile Details Updated",
                            "Updated profile information and preferences"
                    );
                }
            } catch (Exception ex) {
                log.error("Failed to record profile security activity: {}", ex.getMessage(), ex);
            }

            return ResponseEntity.ok(toResponse(result));
        }

        @PutMapping("/me/password")
        public ResponseEntity<Void> changeCurrentPassword(
                @Valid @RequestBody ChangePasswordRequest request,
                HttpServletRequest servletRequest
        ) {
            UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
            changePasswordUseCase.execute(currentUuid, webMapper.toCommand(request));

            try {
                recordSecurityEventUseCase.execute(
                        currentUuid,
                        null,
                        "ACTIVITY",
                        "PASSWORD_CHANGED",
                        "SUCCESS",
                        extractClientIp(servletRequest),
                        null,
                        "Phnom Penh, Cambodia",
                        null,
                        "Password Changed",
                        "Successfully changed account password"
                );
            } catch (Exception ex) {
                log.error("Failed to record password security activity: {}", ex.getMessage(), ex);
            }

            return ResponseEntity.noContent().build();
        }

        @GetMapping("/me/identities")
        public ResponseEntity<List<IdentityResponse>> getCurrentUserIdentities() {
            UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
            List<IdentityResult> results = listUserIdentitiesUseCase.execute(currentUuid);
            return ResponseEntity.ok(results.stream().map(webMapper::toIdentityResponse).toList());
        }

        @DeleteMapping("/me/identities/{identityUuid}")
        public ResponseEntity<Void> unlinkCurrentUserIdentity(
                @PathVariable("identityUuid") UUID identityUuid,
                HttpServletRequest servletRequest
        ) {
            UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());

            // Identify which social account is being unlinked before deletion
            String providerName = "Social";
            String emailOrAccount = "";
            try {
                List<IdentityResult> identities = listUserIdentitiesUseCase.execute(currentUuid);
                var target = identities.stream().filter(i -> i.uuid().equals(identityUuid)).findFirst();
                if (target.isPresent()) {
                    providerName = formatProviderTitle(target.get().provider());
                    if (target.get().email() != null && !target.get().email().isBlank()) {
                        emailOrAccount = " (" + target.get().email() + ")";
                    } else if (target.get().displayName() != null && !target.get().displayName().isBlank()) {
                        emailOrAccount = " (" + target.get().displayName() + ")";
                    } else if (target.get().providerSubject() != null) {
                        emailOrAccount = " (ID: " + target.get().providerSubject() + ")";
                    }
                }
            } catch (Exception ex) {
                log.warn("Could not inspect identity before unlinking: {}", ex.getMessage());
            }

            unlinkIdentityUseCase.execute(currentUuid, identityUuid);

            try {
                recordSecurityEventUseCase.execute(
                        currentUuid,
                        null,
                        "ACTIVITY",
                        "IDENTITY_UNLINKED",
                        "SUCCESS",
                        extractClientIp(servletRequest),
                        null,
                        "Phnom Penh, Cambodia",
                        providerName,
                        providerName + " Account Unlinked",
                        "Disconnected " + providerName + " single sign-on connection" + emailOrAccount
                );
            } catch (Exception ex) {
                log.error("Failed to record unlink security activity: {}", ex.getMessage(), ex);
            }

            return ResponseEntity.noContent().build();
        }

        @GetMapping("/me/login-history")
        public ResponseEntity<List<LoginHistoryResponse>> getMyLoginHistory(
                @RequestParam(name = "status", required = false, defaultValue = "all") String status,
                @RequestParam(name = "limit", required = false, defaultValue = "50") int limit
        ) {
            UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
            var results = listLoginHistoryUseCase.execute(currentUuid, status, limit);
            List<LoginHistoryResponse> responses = results.stream().map(e -> new LoginHistoryResponse(
                    e.uuid().toString(),
                    e.status(),
                    e.createdAt(),
                    e.ipAddress(),
                    e.device(),
                    e.location(),
                    e.authMethod() != null ? e.authMethod() : "Password"
            )).toList();
            return ResponseEntity.ok(responses);
        }

        @GetMapping("/me/security-activity")
        public ResponseEntity<List<SecurityActivityResponse>> getMySecurityActivity(
                @RequestParam(name = "limit", required = false, defaultValue = "50") int limit
        ) {
            UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
            var results = listSecurityActivityUseCase.execute(currentUuid, limit);
            List<SecurityActivityResponse> responses = results.stream().map(e -> {
                String icon = "Activity";
                String color = "text-[#253C95] bg-[#253C95]/10";
                if ("PASSWORD_CHANGED".equalsIgnoreCase(e.eventType())) {
                    icon = "KeyRound";
                    color = "text-emerald-600 bg-emerald-50";
                } else if ("USERNAME_CHANGED".equalsIgnoreCase(e.eventType()) || "PROFILE_UPDATED".equalsIgnoreCase(e.eventType())) {
                    icon = "UserCheck";
                    color = "text-[#253C95] bg-[#253C95]/10";
                } else if ("IDENTITY_LINKED".equalsIgnoreCase(e.eventType())) {
                    icon = "Activity";
                    color = "text-[#253C95] bg-[#253C95]/10";
                } else if ("IDENTITY_UNLINKED".equalsIgnoreCase(e.eventType()) || "SESSION_TERMINATED".equalsIgnoreCase(e.eventType())) {
                    icon = "Lock";
                    color = "text-[#DF1F28] bg-[#DF1F28]/10";
                }

                return new SecurityActivityResponse(
                        e.uuid().toString(),
                        e.description() != null ? e.description() : e.eventType(),
                        e.details() != null ? e.details() : (e.description() != null ? e.description() : "Account updated"),
                        e.createdAt(),
                        icon,
                        color
                );
            }).toList();
            return ResponseEntity.ok(responses);
        }

        @PostMapping("/me/deactivate-request")
        public ResponseEntity<Void> requestAccountDeactivation(
                @RequestBody(required = false) java.util.Map<String, String> body,
                HttpServletRequest servletRequest
        ) {
            UUID currentUuid = UUID.fromString(currentActorProvider.getCurrentActorSub());
            UserResult user = getUserUseCase.execute(currentUuid);
            String reason = (body != null && body.get("reason") != null && !body.get("reason").isBlank())
                    ? body.get("reason")
                    : "User requested account deactivation";

            try {
                recordSecurityEventUseCase.execute(
                        currentUuid,
                        user.username(),
                        "ACTIVITY",
                        "DEACTIVATION_REQUESTED",
                        "PENDING",
                        extractClientIp(servletRequest),
                        servletRequest.getHeader("User-Agent"),
                        "Phnom Penh, Cambodia",
                        null,
                        "Account Deactivation Requested",
                        "User @" + user.username() + " submitted an account deactivation request. Reason: " + reason
                );
            } catch (Exception ex) {
                log.error("Failed to record deactivation request: {}", ex.getMessage());
            }

            return ResponseEntity.noContent().build();
        }

        private String formatProviderTitle(String provider) {
            if (provider == null || provider.isBlank()) return "Social";
            String lower = provider.trim().toLowerCase();
            return switch (lower) {
                case "google" -> "Google";
                case "github" -> "GitHub";
                case "facebook" -> "Facebook";
                case "telegram" -> "Telegram";
                default -> provider.substring(0, 1).toUpperCase() + provider.substring(1).toLowerCase();
            };
        }

        private String extractClientIp(HttpServletRequest request) {
            if (request == null) return "127.0.0.1 (Localhost)";
            String ip = null;
            String xf = request.getHeader("X-Forwarded-For");
            if (xf != null && !xf.isBlank() && !"unknown".equalsIgnoreCase(xf)) {
                ip = xf.split(",")[0].trim();
            }
            if (ip == null || ip.isBlank()) {
                String xr = request.getHeader("X-Real-IP");
                if (xr != null && !xr.isBlank() && !"unknown".equalsIgnoreCase(xr)) {
                    ip = xr.trim();
                }
            }
            if (ip == null || ip.isBlank()) {
                ip = request.getRemoteAddr();
            }
            if (ip == null || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1") || ip.equals("127.0.0.1")) {
                return "127.0.0.1 (Localhost)";
            }
            return ip;
        }

        // --- Helpers ---

        private UserResponse toResponse(UserResult result) {
            UserAuthoritiesResult authorities = getUserAuthoritiesUseCase.execute(result.uuid());
            return webMapper.toUserResponse(result, authorities);
        }
    }