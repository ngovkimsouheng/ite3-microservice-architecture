package kh.edu.istad.iam.domain.usecase.permission;

import kh.edu.istad.iam.domain.dto.CreatePermissionCommand;
import kh.edu.istad.iam.domain.dto.PermissionResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;
    private final DomainDtoMapper domainDtoMapper;
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

        return domainDtoMapper.toPermissionResult(saved);
    }
}
