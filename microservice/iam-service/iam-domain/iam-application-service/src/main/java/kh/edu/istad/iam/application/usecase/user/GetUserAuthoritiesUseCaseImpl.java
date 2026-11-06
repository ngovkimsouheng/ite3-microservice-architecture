package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.UserAuthoritiesResult;
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
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUuid));

        List<UserRoleAssignment> roleAssignments = userRepository.findRoleAssignmentsByUserId(user.getId());
        Set<String> roleAuthorities = new HashSet<>();
        Set<String> rolePermissions = new HashSet<>();

        for (UserRoleAssignment assignment : roleAssignments) {
            if (assignment.isActive()) {
                roleRepository.findById(assignment.getRoleId()).ifPresent(role -> {
                    if (role.isActive() && !role.isDeleted()) {
                        roleAuthorities.add(role.getAuthority()); // ROLE_ + name

                        List<RolePermissionAssignment> permAssignments = roleRepository.findPermissionAssignmentsByRoleId(role.getId());
                        for (RolePermissionAssignment rpa : permAssignments) {
                            if (rpa.isActive()) {
                                permissionRepository.findById(rpa.getPermissionId())
                                        .ifPresent(p -> rolePermissions.add(p.getAuthority()));
                            }
                        }
                    }
                });
            }
        }

        List<UserPermissionAssignment> directPerms = userRepository.findPermissionAssignmentsByUserId(user.getId());
        Map<Long, String> permLookup = new HashMap<>();
        for (UserPermissionAssignment upa : directPerms) {
            permissionRepository.findById(upa.getPermissionId())
                    .ifPresent(p -> permLookup.put(p.getId(), p.getAuthority()));
        }

        // Rule B.6: active role permissions + direct GRANT - direct DENY
        Set<String> effectivePermissions = user.calculateEffectivePermissions(rolePermissions, directPerms, permLookup);

        return new UserAuthoritiesResult(user.getUuid(), roleAuthorities, effectivePermissions);
    }
}
