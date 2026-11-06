package kh.edu.istad.iam.persistence.repository;

import kh.edu.istad.iam.persistence.entity.RolePermissionAssignmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionAssignmentJpaRepository extends JpaRepository<RolePermissionAssignmentJpaEntity, Long> {
    List<RolePermissionAssignmentJpaEntity> findByRoleId(Long roleId);
    Optional<RolePermissionAssignmentJpaEntity> findByRoleIdAndPermissionId(Long roleId, Long permissionId);
}