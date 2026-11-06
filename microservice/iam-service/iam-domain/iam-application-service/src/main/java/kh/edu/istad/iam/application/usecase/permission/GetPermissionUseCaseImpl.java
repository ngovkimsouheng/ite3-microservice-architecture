package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.dto.CommonDtos.PermissionResult;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPermissionUseCaseImpl implements GetPermissionUseCase {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResult execute(UUID permissionUuid) {
        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionUuid));

        return new PermissionResult(
                permission.getUuid(),
                permission.getName(),
                permission.getResource(),
                permission.getAction(),
                permission.getDescription(),
                permission.isSystem(),
                permission.isActive()
        );
    }
}
