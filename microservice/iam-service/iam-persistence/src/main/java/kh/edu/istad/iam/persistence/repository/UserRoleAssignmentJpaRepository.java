package kh.edu.istad.iam.persistence.repository;

import kh.edu.istad.iam.persistence.entity.UserRoleAssignmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRoleAssignmentJpaRepository extends JpaRepository<UserRoleAssignmentJpaEntity, Long> {
    List<UserRoleAssignmentJpaEntity> findByUserId(Long userId);
    Optional<UserRoleAssignmentJpaEntity> findByUserIdAndRoleId(Long userId, Long roleId);
}
