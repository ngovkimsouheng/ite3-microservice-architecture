package kh.edu.istad.iam.application.port.out;

import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserRoleAssignment;
import kh.edu.istad.iam.domain.core.entity.UserPermissionAssignment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUuid(UUID uuid);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Assignment methods (no @ManyToMany)
    void saveRoleAssignment(UserRoleAssignment assignment);
    List<UserRoleAssignment> findRoleAssignmentsByUserId(Long userId);
    Optional<UserRoleAssignment> findRoleAssignment(Long userId, Long roleId);

    void savePermissionAssignment(UserPermissionAssignment assignment);
    List<UserPermissionAssignment> findPermissionAssignmentsByUserId(Long userId);
    Optional<UserPermissionAssignment> findPermissionAssignment(Long userId, Long permissionId);
}