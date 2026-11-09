package kh.edu.istad.iam.domain.usecase.permission;

import kh.edu.istad.iam.domain.dto.PermissionResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final DomainDtoMapper domainDtoMapper;

    public PermissionResult execute(UUID permissionUuid) {
        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionUuid));

        return domainDtoMapper.toPermissionResult(permission);
    }
}
