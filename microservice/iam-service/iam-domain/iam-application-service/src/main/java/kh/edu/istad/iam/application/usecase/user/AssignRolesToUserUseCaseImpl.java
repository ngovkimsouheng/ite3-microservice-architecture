package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.AssignRolesToUserCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.RoleAssignmentResult;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignRolesToUserUseCaseImpl implements AssignRolesToUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public List<RoleAssignmentResult> execute(AssignRolesToUserCommand command) {
        User user = userRepository.findByUuid(command.userUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userUuid()));

        String actor = currentActorProvider.getCurrentActorSub();
        OffsetDateTime now = OffsetDateTime.now();
        List<RoleAssignmentResult> results = new ArrayList<>();

        for (UUID roleUuid : command.roleUuids()) {
            Role role = roleRepository.findByUuid(roleUuid)
                    .filter(r -> !r.isDeleted())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleUuid));

            if (!role.isActive()) {
                throw new IllegalStateException("Inactive roles cannot be assigned: " + role.getName());
            }

            var existing = userRepository.findRoleAssignment(user.getId(), role.getId());
            if (existing.isPresent() && existing.get().getRevokedAt() == null) {
                continue;
            }

            UserRoleAssignment assignment = UserRoleAssignment.builder()
                    .userId(user.getId())
                    .roleId(role.getId())
                    .assignedAt(now)
                    .assignedBy(actor)
                    .expiresAt(command.expiresAt())
                    .reason(command.reason())
                    .build();

            userRepository.saveRoleAssignment(assignment);

            results.add(new RoleAssignmentResult(
                    role.getUuid(),
                    role.getName(),
                    assignment.getAssignedAt(),
                    assignment.getExpiresAt()
            ));
        }

        return results;
    }
}