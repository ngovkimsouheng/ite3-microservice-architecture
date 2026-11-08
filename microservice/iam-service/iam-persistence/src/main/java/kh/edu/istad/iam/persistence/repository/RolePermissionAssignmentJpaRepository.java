package kh.edu.istad.iam.persistence.repository;

import kh.edu.istad.iam.persistence.entity.RolePermissionAssignmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolePermissionAssignmentJpaRepository extends JpaRepository<RolePermissionAssignmentJpaEntity, Long> {

    // Active role-permission assignments (where revokedAt IS NULL)
    List<RolePermissionAssignmentJpaEntity> findByRoleIdAndRevokedAtIsNull(Long roleId);

    // General queries expected by RoleRepositoryAdapter
    List<RolePermissionAssignmentJpaEntity> findByRoleId(Long roleId);

    Optional<RolePermissionAssignmentJpaEntity> findByRoleIdAndPermissionId(Long roleId, Long permissionId);

    @Query("SELECT r FROM RolePermissionAssignmentJpaEntity r " +
            "WHERE r.roleId = :roleId " +
            "AND r.permissionId = :permissionId " +
            "AND r.revokedAt IS NULL")
    Optional<RolePermissionAssignmentJpaEntity> findActive(@Param("roleId") Long roleId,
                                                           @Param("permissionId") Long permissionId);
}