package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.GrantPermissionsToUserCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.PermissionGrantResult;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserPermissionAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrantPermissionsToUserUseCaseImpl implements GrantPermissionsToUserUseCase {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public List<PermissionGrantResult> execute(GrantPermissionsToUserCommand command) {
        User user = userRepository.findByUuid(command.userUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userUuid()));

        String actor = currentActorProvider.getCurrentActorSub();
        OffsetDateTime now = OffsetDateTime.now();
        List<PermissionGrantResult> results = new ArrayList<>();

        for (UUID permUuid : command.permissionUuids()) {
            Permission perm = permissionRepository.findByUuid(permUuid)
                    .filter(p -> !p.isDeleted())
                    .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permUuid));

            if (!perm.isActive()) {
                throw new IllegalStateException("Inactive permissions cannot be assigned: " + perm.getName());
            }

            var existing = userRepository.findPermissionAssignment(user.getId(), perm.getId());
            if (existing.isPresent() && existing.get().getRevokedAt() == null) {
                continue;
            }

            UserPermissionAssignment assignment = UserPermissionAssignment.builder()
                    .userId(user.getId())
                    .permissionId(perm.getId())
                    .effect(command.effect())
                    .assignedAt(now)
                    .assignedBy(actor)
                    .expiresAt(command.expiresAt())
                    .reason(command.reason())
                    .build();

            userRepository.savePermissionAssignment(assignment);

            results.add(new PermissionGrantResult(
                    perm.getUuid(),
                    perm.getName(),
                    assignment.getEffect(),
                    assignment.getAssignedAt()
            ));
        }

        return results;
    }
}