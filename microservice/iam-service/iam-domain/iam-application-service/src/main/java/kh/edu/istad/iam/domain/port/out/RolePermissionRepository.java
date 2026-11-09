package kh.edu.istad.iam.domain.port.out;

import kh.edu.istad.iam.domain.core.entity.RolePermissionAssignment;
import java.util.Optional;

public interface RolePermissionRepository {
    Optional<RolePermissionAssignment> findActive(Long roleId, Long permissionId);
    RolePermissionAssignment save(RolePermissionAssignment assignment);
}
