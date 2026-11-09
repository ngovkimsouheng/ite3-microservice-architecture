package kh.edu.istad.iam.domain.mapper;

import kh.edu.istad.iam.domain.core.entity.*;
import kh.edu.istad.iam.domain.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DomainDtoMapper {

    UserResult toUserResult(User user);

    UserSummary toUserSummary(User user);

    @Mapping(target = "isSystem", expression = "java(role.isSystem())")
    @Mapping(target = "active", expression = "java(role.isActive())")
    RoleResult toRoleResult(Role role);

    @Mapping(target = "active", expression = "java(role.isActive())")
    RoleSummary toRoleSummary(Role role);

    @Mapping(target = "isSystem", expression = "java(permission.isSystem())")
    @Mapping(target = "active", expression = "java(permission.isActive())")
    PermissionResult toPermissionResult(Permission permission);

    IdentityResult toIdentityResult(Identity identity);

    UserRoleResult toUserRoleResult(Role role);

    @Mapping(target = "roleUuid", source = "role.uuid")
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "assignedAt", source = "assignment.assignedAt")
    @Mapping(target = "expiresAt", source = "assignment.expiresAt")
    RoleAssignmentResult toRoleAssignmentResult(Role role, UserRoleAssignment assignment);

    @Mapping(target = "permissionUuid", source = "permission.uuid")
    @Mapping(target = "permissionName", source = "permission.name")
    @Mapping(target = "effect", source = "assignment.effect")
    @Mapping(target = "assignedAt", source = "assignment.assignedAt")
    PermissionGrantResult toPermissionGrantResult(
            Permission permission,
            UserPermissionAssignment assignment
    );

    @Mapping(target = "permissionUuid", source = "permission.uuid")
    @Mapping(target = "permissionName", source = "permission.name")
    @Mapping(target = "assignedAt", source = "assignment.assignedAt")
    PermissionAssignmentResult toPermissionAssignmentResult(
            Permission permission,
            RolePermissionAssignment assignment
    );
}
