package kh.edu.istad.iam.domain.usecase.permission;

import kh.edu.istad.iam.domain.dto.PermissionResult;
import kh.edu.istad.iam.domain.dto.UpdatePermissionCommand;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UpdatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;
    private final DomainDtoMapper domainDtoMapper;
    @Transactional
    public PermissionResult execute(UpdatePermissionCommand command) {
        Permission permission = permissionRepository.findByUuid(command.permissionUuid())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with UUID: " + command.permissionUuid()));

        if (permission.isSystem()) {
            throw new IllegalStateException("System permissions cannot be modified.");
        }

        permission.setDescription(command.description());
        permission.setUpdatedAt(OffsetDateTime.now());
        permission.setUpdatedBy(currentActorProvider.getCurrentActorSub());

        Permission updated = permissionRepository.save(permission);

        return domainDtoMapper.toPermissionResult(updated);
    }
}
