package kh.edu.istad.iam.application.usecase.permission;

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
public class DeactivatePermissionUseCaseImpl implements DeactivatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public void execute(UUID permissionUuid) {
        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionUuid));

        if (permission.isSystem()) {
            throw new IllegalStateException("System permissions cannot be deactivated");
        }

        permission.setActive(false);
        permission.setUpdatedAt(OffsetDateTime.now());
        permission.setUpdatedBy(currentActorProvider.getCurrentActorSub());

        permissionRepository.save(permission);
    }
}