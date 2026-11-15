package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.dto.GrantPermissionsToUserCommand;
import kh.edu.istad.iam.domain.dto.PermissionGrantResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserPermissionAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GrantPermissionsToUserUseCase {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;
    private final DomainDtoMapper domainDtoMapper;

    @Transactional
    public List<PermissionGrantResult> execute(GrantPermissionsToUserCommand command) {
        User user = userRepository.findByUuid(command.userUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userUuid()));

        String actor = currentActorProvider.getCurrentActorSub();
        OffsetDateTime now = OffsetDateTime.now();
        List<PermissionGrantResult> results = new ArrayList<>();

        Set<UUID> requestedPermUuids = command.permissionUuids() != null
                ? new LinkedHashSet<>(command.permissionUuids())
                : Collections.emptySet();

        Set<Long> targetPermIds = new HashSet<>();
        List<Permission> targetPerms = new ArrayList<>();

        for (UUID permUuid : requestedPermUuids) {
            Permission perm = permissionRepository.findByUuid(permUuid)
                    .filter(p -> !p.isDeleted())
                    .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permUuid));

            if (!perm.isActive()) {
                throw new IllegalStateException("Inactive permissions cannot be assigned: " + perm.getName());
            }

            targetPermIds.add(perm.getId());
            targetPerms.add(perm);
        }

        // 1. Revoke any currently active direct permissions not in target list
        List<UserPermissionAssignment> currentAssignments = userRepository.findPermissionAssignmentsByUserId(user.getId());
        for (UserPermissionAssignment assignment : currentAssignments) {
            if (assignment.isActive() && !targetPermIds.contains(assignment.getPermissionId())) {
                assignment.revoke(actor, "Unassigned by administrator");
                userRepository.savePermissionAssignment(assignment);
            }
        }

        // 2. Grant / re-activate target permissions
        for (Permission perm : targetPerms) {
            var existing = userRepository.findPermissionAssignment(user.getId(), perm.getId());
            if (existing.isPresent() && existing.get().isActive()) {
                results.add(domainDtoMapper.toPermissionGrantResult(perm, existing.get()));
                continue;
            }

            UserPermissionAssignment assignment = UserPermissionAssignment.builder()
                    .id(existing.map(UserPermissionAssignment::getId).orElse(null))
                    .userId(user.getId())
                    .permissionId(perm.getId())
                    .effect(command.effect() != null ? command.effect() : "GRANT")
                    .assignedAt(now)
                    .assignedBy(actor)
                    .expiresAt(command.expiresAt())
                    .revokedAt(null)
                    .revokedBy(null)
                    .reason(command.reason())
                    .build();

            userRepository.savePermissionAssignment(assignment);
            results.add(domainDtoMapper.toPermissionGrantResult(perm, assignment));
        }

        return results;
    }
}
