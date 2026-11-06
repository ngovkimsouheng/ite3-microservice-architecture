package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.dto.CommonDtos.CreateRoleCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.RoleResult;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCaseImpl implements CreateRoleUseCase {

    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public RoleResult execute(CreateRoleCommand command) {
        String roleName = command.name().toUpperCase().trim();
        if (roleRepository.existsByName(roleName)) {
            throw new IllegalArgumentException("Role already exists: " + roleName);
        }

        String actor = currentActorProvider.getCurrentActorSub();
        OffsetDateTime now = OffsetDateTime.now();

        Role role = Role.builder()
                .uuid(UUID.randomUUID())
                .name(roleName)
                .description(command.description())
                .isSystem(false)
                .isActive(true)
                .isDeleted(false)
                .createdAt(now)
                .createdBy(actor)
                .updatedAt(now)
                .updatedBy(actor)
                .version(0L)
                .build();

        Role saved = roleRepository.save(role);

        return new RoleResult(
                saved.getUuid(),
                saved.getName(),
                saved.getDescription(),
                saved.isSystem(),
                saved.isActive()
        );
    }
}
