package kh.edu.istad.iam.application.usecase.user;

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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevokePermissionFromUserUseCaseImpl implements RevokePermissionFromUserUseCase {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public void execute(UUID userUuid, UUID permissionUuid, String reason) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUuid));
        Permission perm = permissionRepository.findByUuid(permissionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionUuid));

        UserPermissionAssignment assignment = userRepository.findPermissionAssignment(user.getId(), perm.getId())
                .filter(a -> a.getRevokedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Active permission assignment not found"));

        assignment.setRevokedAt(OffsetDateTime.now());
        assignment.setRevokedBy(currentActorProvider.getCurrentActorSub());
        assignment.setReason(reason);

        userRepository.savePermissionAssignment(assignment);
    }
}