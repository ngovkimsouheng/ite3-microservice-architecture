package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.dto.CommonDtos.CreatePermissionCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.PermissionResult;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreatePermissionUseCaseImpl implements CreatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public PermissionResult execute(CreatePermissionCommand command) {
        String resource = command.resource().toLowerCase().trim();
        String action = command.action().toLowerCase().trim();
        String name = resource + ":" + action; // Rule: resource:action

        if (permissionRepository.existsByName(name)) {
            throw new IllegalArgumentException("Permission already exists: " + name);
        }

        String actor = currentActorProvider.getCurrentActorSub();
        OffsetDateTime now = OffsetDateTime.now();

        Permission permission = Permission.builder()
                .uuid(UUID.randomUUID())
                .name(name)
                .resource(resource)
                .action(action)
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

        Permission saved = permissionRepository.save(permission);

        return new PermissionResult(
                saved.getUuid(),
                saved.getName(),
                saved.getResource(),
                saved.getAction(),
                saved.getDescription(),
                saved.isSystem(),
                saved.isActive()
        );
    }
}
