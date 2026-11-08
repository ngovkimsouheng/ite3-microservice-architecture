package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.port.out.RolePermissionRepository;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.RolePermissionAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevokePermissionFromRoleUseCaseImpl implements RevokePermissionFromRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void execute(UUID roleUuid, UUID permissionUuid, String reason) {
        Role role = roleRepository.findByUuid(roleUuid)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleUuid));

        if (role.isSystem()) {
            throw new IllegalStateException("Cannot revoke permissions from a system role");
        }

        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionUuid));

        RolePermissionAssignment assignment = rolePermissionRepository.findActive(role.getId(), permission.getId())
                .orElseThrow(() -> new IllegalArgumentException("Active permission assignment not found"));

        assignment.setRevokedAt(OffsetDateTime.now());
        assignment.setReason(reason != null ? reason : "Revoked by admin");
        rolePermissionRepository.save(assignment);
    }
}