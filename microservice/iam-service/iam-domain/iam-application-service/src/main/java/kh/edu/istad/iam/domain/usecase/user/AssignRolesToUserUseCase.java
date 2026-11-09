package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.dto.AssignRolesToUserCommand;
import kh.edu.istad.iam.domain.dto.RoleAssignmentResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserRoleAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignRolesToUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;
    private final DomainDtoMapper domainDtoMapper;

    @Transactional
    public List<RoleAssignmentResult> execute(AssignRolesToUserCommand command) {
        User user = userRepository.findByUuid(command.userUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userUuid()));

        String actor = currentActorProvider.getCurrentActorSub();
        OffsetDateTime now = OffsetDateTime.now();
        List<RoleAssignmentResult> results = new ArrayList<>();

        for (UUID roleUuid : new LinkedHashSet<>(command.roleUuids())) {
            Role role = roleRepository.findByUuid(roleUuid)
                    .filter(r -> !r.isDeleted())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleUuid));

            if (!role.isActive()) {
                throw new IllegalStateException("Inactive roles cannot be assigned: " + role.getName());
            }

            var existing = userRepository.findRoleAssignment(user.getId(), role.getId());
            if (existing.isPresent() && existing.get().isActive()) {
                UserRoleAssignment activeAssignment = existing.get();
                results.add(domainDtoMapper.toRoleAssignmentResult(role, activeAssignment));
                continue;
            }

            UserRoleAssignment assignment = UserRoleAssignment.builder()
                    .id(existing.map(UserRoleAssignment::getId).orElse(null))
                    .userId(user.getId())
                    .roleId(role.getId())
                    .assignedAt(now)
                    .assignedBy(actor)
                    .expiresAt(command.expiresAt())
                    .revokedAt(null)
                    .revokedBy(null)
                    .reason(command.reason())
                    .build();

            userRepository.saveRoleAssignment(assignment);

            results.add(domainDtoMapper.toRoleAssignmentResult(role, assignment));
        }

        return results;
    }
}
