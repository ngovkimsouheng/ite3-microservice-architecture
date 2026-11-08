
package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.UserAuthoritiesResult;
import kh.edu.istad.iam.application.dto.CommonDtos.UserRoleResult;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GetUserAuthoritiesUseCaseImpl implements GetUserAuthoritiesUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public UserAuthoritiesResult execute(UUID userUuid) {

        // 1. Find user
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found: " + userUuid));

        // 2. Get user role assignments
        List<UserRoleAssignment> roleAssignments =
                userRepository.findRoleAssignmentsByUserId(user.getId());

        Set<String> roleAuthorities = new HashSet<>();
        Set<String> rolePermissions = new HashSet<>();

        // Store role UUID and name for API response
        Map<UUID, UserRoleResult> roleDetails = new LinkedHashMap<>();

        // 3. Collect active roles and their permissions
        for (UserRoleAssignment assignment : roleAssignments) {

            if (assignment.isActive()) {

                roleRepository.findById(assignment.getRoleId())
                        .ifPresent(role -> {

                            if (role.isActive() && !role.isDeleted()) {

                                // ROLE_ + role name
                                roleAuthorities.add(role.getAuthority());

                                // Role details for UserResponse
                                roleDetails.put(
                                        role.getUuid(),
                                        new UserRoleResult(
                                                role.getUuid(),
                                                role.getName()
                                        )
                                );

                                // Get permissions assigned to this role
                                List<RolePermissionAssignment> permAssignments =
                                        roleRepository.findPermissionAssignmentsByRoleId(
                                                role.getId()
                                        );

                                for (RolePermissionAssignment rpa : permAssignments) {

                                    if (rpa.isActive()) {
                                        permissionRepository.findById(rpa.getPermissionId())
                                                .ifPresent(permission ->
                                                        rolePermissions.add(
                                                                permission.getAuthority()
                                                        )
                                                );
                                    }
                                }
                            }
                        });
            }
        }

        // 4. Get direct user permission assignments
        List<UserPermissionAssignment> directPerms =
                userRepository.findPermissionAssignmentsByUserId(user.getId());

        Map<Long, String> permLookup = new HashMap<>();

        for (UserPermissionAssignment upa : directPerms) {

            permissionRepository.findById(upa.getPermissionId())
                    .ifPresent(permission ->
                            permLookup.put(
                                    permission.getId(),
                                    permission.getAuthority()
                            )
                    );
        }

        // 5. Calculate effective permissions
        // Rule B.6: active role permissions + direct GRANT - direct DENY
        Set<String> effectivePermissions =
                user.calculateEffectivePermissions(
                        rolePermissions,
                        directPerms,
                        permLookup
                );

        // 6. Return authorities and role details
        return new UserAuthoritiesResult(
                user.getUuid(),
                roleAuthorities,
                effectivePermissions,
                new ArrayList<>(roleDetails.values())
        );
    }
}
