package kh.edu.istad.iam.application.port.out;

import kh.edu.istad.iam.application.dto.CommonDtos.PageResult;
import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.RolePermissionAssignment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(Long id);
    Optional<Role> findByUuid(UUID uuid);
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    List<Role> findAllActive();
    PageResult<Role> findAll(int page, int size, String sort);

    void savePermissionAssignment(RolePermissionAssignment assignment);
    List<RolePermissionAssignment> findPermissionAssignmentsByRoleId(Long roleId);
    Optional<RolePermissionAssignment> findPermissionAssignment(Long roleId, Long permissionId);
}