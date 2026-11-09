package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.domain.port.out.RolePermissionRepository;
import kh.edu.istad.iam.domain.core.entity.RolePermissionAssignment;
import kh.edu.istad.iam.persistence.mapper.RolePersistenceMapper;
import kh.edu.istad.iam.persistence.repository.RolePermissionAssignmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RolePermissionRepositoryAdapter implements RolePermissionRepository {

    private final RolePermissionAssignmentJpaRepository jpaRepository;
    private final RolePersistenceMapper roleMapper;

    @Override
    public Optional<RolePermissionAssignment> findActive(Long roleId, Long permissionId) {
        return jpaRepository.findActive(roleId, permissionId).map(roleMapper::toDomain);
    }

    @Override
    public RolePermissionAssignment save(RolePermissionAssignment domain) {
        return roleMapper.toDomain(jpaRepository.save(roleMapper.toEntity(domain)));
    }
}
