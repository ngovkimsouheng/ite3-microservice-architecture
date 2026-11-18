package kh.edu.istad.iam.domain.usecase.role;

import kh.edu.istad.iam.domain.core.entity.Permission;
import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.RolePermissionAssignment;
import kh.edu.istad.iam.domain.dto.PermissionResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetRolePermissionsUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final DomainDtoMapper domainDtoMapper;

    @Transactional(readOnly = true)
    public List<PermissionResult> execute(UUID roleUuid) {
        Role role = roleRepository.findByUuid(roleUuid)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleUuid));

        List<RolePermissionAssignment> assignments = roleRepository.findPermissionAssignmentsByRoleId(role.getId());
        List<PermissionResult> results = new ArrayList<>();

        for (RolePermissionAssignment assignment : assignments) {
            if (assignment.getRevokedAt() == null) {
                permissionRepository.findById(assignment.getPermissionId())
                        .filter(p -> !p.isDeleted())
                        .ifPresent(p -> results.add(domainDtoMapper.toPermissionResult(p)));
            }
        }

        return results;
    }
}
