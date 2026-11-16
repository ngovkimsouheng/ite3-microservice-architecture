package kh.edu.istad.iam.domain.usecase.role;

import kh.edu.istad.iam.domain.dto.AssignPermissionsToRoleCommand;
import kh.edu.istad.iam.domain.dto.PermissionAssignmentResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.RolePermissionAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignPermissionsToRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;
    private final DomainDtoMapper domainDtoMapper;

    @Transactional
    public List<PermissionAssignmentResult> execute(AssignPermissionsToRoleCommand command) {
        Role role = roleRepository.findByUuid(command.roleUuid())
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + command.roleUuid()));

        String actor = currentActorProvider.getCurrentActorSub();
        OffsetDateTime now = OffsetDateTime.now();
        List<PermissionAssignmentResult> results = new ArrayList<>();
        Set<UUID> desiredPermUuids = command.permissionUuids() != null
                ? new HashSet<>(command.permissionUuids())
                : java.util.Collections.emptySet();

        // 1. Revoke any currently active assignments that were unchecked by admin
        List<RolePermissionAssignment> existingAssignments = roleRepository
                .findPermissionAssignmentsByRoleId(role.getId());
        for (RolePermissionAssignment assignment : existingAssignments) {
            if (assignment.getRevokedAt() == null) {
                Permission perm = permissionRepository.findById(assignment.getPermissionId()).orElse(null);
                if (perm != null && !desiredPermUuids.contains(perm.getUuid())) {
                    assignment.setRevokedAt(now);
                    assignment.setRevokedBy(actor);
                    roleRepository.savePermissionAssignment(assignment);
                }
            }
        }

        // 2. Assign or re-activate checked permissions
        for (UUID permUuid : desiredPermUuids) {
            Permission perm = permissionRepository.findByUuid(permUuid)
                    .filter(p -> !p.isDeleted())
                    .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permUuid));

            if (!perm.isActive()) {
                throw new IllegalStateException("Inactive permissions cannot be assigned: " + perm.getName());
            }

            var existing = roleRepository.findPermissionAssignment(role.getId(), perm.getId());
            if (existing.isPresent()) {
                RolePermissionAssignment rpa = existing.get();
                if (rpa.getRevokedAt() != null) {
                    rpa.setRevokedAt(null);
                    rpa.setRevokedBy(null);
                    rpa.setAssignedAt(now);
                    rpa.setAssignedBy(actor);
                    rpa.setReason(command.reason());
                    roleRepository.savePermissionAssignment(rpa);
                }
                results.add(domainDtoMapper.toPermissionAssignmentResult(perm, rpa));
            } else {
                RolePermissionAssignment assignment = RolePermissionAssignment.builder()
                        .roleId(role.getId())
                        .permissionId(perm.getId())
                        .assignedAt(now)
                        .assignedBy(actor)
                        .reason(command.reason())
                        .build();

                roleRepository.savePermissionAssignment(assignment);
                results.add(domainDtoMapper.toPermissionAssignmentResult(perm, assignment));
            }
        }

        return results;
    }
}
