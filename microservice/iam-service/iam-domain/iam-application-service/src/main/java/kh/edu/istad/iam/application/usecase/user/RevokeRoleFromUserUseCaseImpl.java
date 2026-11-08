package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserRoleAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevokeRoleFromUserUseCaseImpl implements RevokeRoleFromUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public void execute(UUID userUuid, UUID roleUuid, String reason) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUuid));
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleUuid));

        UserRoleAssignment assignment = userRepository.findRoleAssignment(user.getId(), role.getId())
                .filter(a -> a.getRevokedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Active role assignment not found"));

        assignment.setRevokedAt(OffsetDateTime.now());
        assignment.setRevokedBy(currentActorProvider.getCurrentActorSub());
        assignment.setReason(reason);

        userRepository.saveRoleAssignment(assignment);
    }
}