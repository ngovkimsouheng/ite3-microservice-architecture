package kh.edu.istad.iam.persistence.repository;

import kh.edu.istad.iam.persistence.entity.UserPermissionAssignmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserPermissionAssignmentJpaRepository extends JpaRepository<UserPermissionAssignmentJpaEntity, Long> {
    List<UserPermissionAssignmentJpaEntity> findByUserId(Long userId);
    Optional<UserPermissionAssignmentJpaEntity> findByUserIdAndPermissionId(Long userId, Long permissionId);
}
