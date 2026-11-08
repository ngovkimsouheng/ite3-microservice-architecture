package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.application.port.out.RolePermissionRepository;
import kh.edu.istad.iam.domain.core.entity.RolePermissionAssignment;
import kh.edu.istad.iam.persistence.entity.RolePermissionAssignmentJpaEntity;
import kh.edu.istad.iam.persistence.repository.RolePermissionAssignmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RolePermissionRepositoryAdapter implements RolePermissionRepository {

    private final RolePermissionAssignmentJpaRepository jpaRepository;

    @Override
    public Optional<RolePermissionAssignment> findActive(Long roleId, Long permissionId) {
        return jpaRepository.findActive(roleId, permissionId).map(this::toDomain);
    }

    @Override
    public RolePermissionAssignment save(RolePermissionAssignment domain) {
        RolePermissionAssignmentJpaEntity entity = toEntity(domain);
        RolePermissionAssignmentJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private RolePermissionAssignment toDomain(RolePermissionAssignmentJpaEntity entity) {
        return RolePermissionAssignment.builder()
                .id(entity.getId())
                .roleId(entity.getRoleId())
                .permissionId(entity.getPermissionId())
                .assignedAt(entity.getAssignedAt())
                .assignedBy(entity.getAssignedBy())
                .expiresAt(entity.getExpiresAt())
                .revokedAt(entity.getRevokedAt())
                .revokedBy(entity.getRevokedBy())
                .reason(entity.getReason())
                .build();
    }

    private RolePermissionAssignmentJpaEntity toEntity(RolePermissionAssignment domain) {
        return RolePermissionAssignmentJpaEntity.builder()
                .id(domain.getId())
                .roleId(domain.getRoleId())
                .permissionId(domain.getPermissionId())
                .assignedAt(domain.getAssignedAt())
                .assignedBy(domain.getAssignedBy())
                .expiresAt(domain.getExpiresAt())
                .revokedAt(domain.getRevokedAt())
                .revokedBy(domain.getRevokedBy())
                .reason(domain.getReason())
                .build();
    }
}