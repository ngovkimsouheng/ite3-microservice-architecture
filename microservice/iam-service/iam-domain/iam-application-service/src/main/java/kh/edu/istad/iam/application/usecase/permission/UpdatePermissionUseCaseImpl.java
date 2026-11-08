package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.dto.CommonDtos.PermissionResult;
import kh.edu.istad.iam.application.dto.CommonDtos.UpdatePermissionCommand;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UpdatePermissionUseCaseImpl implements UpdatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    @Override
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

        return new PermissionResult(
                updated.getUuid(),
                updated.getName(),
                updated.getResource(),
                updated.getAction(),
                updated.getDescription(),
                updated.isSystem(),
                updated.isActive()
        );
    }
}