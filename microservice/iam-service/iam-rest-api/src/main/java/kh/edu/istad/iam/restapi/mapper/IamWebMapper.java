package kh.edu.istad.iam.restapi.mapper;

import kh.edu.istad.iam.domain.dto.*;
import kh.edu.istad.iam.restapi.dto.*;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IamWebMapper {

    RegisterUserCommand toCommand(RegisterUserRequest request);

    @Mapping(target = "addressStreetAddress", source = "address.streetAddress")
    @Mapping(target = "addressLocality", source = "address.locality")
    @Mapping(target = "addressRegion", source = "address.region")
    @Mapping(target = "addressPostalCode", source = "address.postalCode")
    @Mapping(target = "addressCountry", source = "address.country")
    CreateUserCommand toCommand(CreateUserRequest request);

    @Mapping(target = "userUuid", source = "uuid")
    UpdateUserProfileCommand toCommand(UUID uuid, UpdateUserProfileRequest request);

    ChangePasswordCommand toCommand(ChangePasswordRequest request);

    @Mapping(target = "userUuid", source = "uuid")
    ResetUserPasswordCommand toCommand(UUID uuid, ResetUserPasswordRequest request);

    @Mapping(target = "userUuid", source = "uuid")
    AssignRolesToUserCommand toCommand(UUID uuid, AssignRolesToUserRequest request);

    @Mapping(target = "userUuid", source = "uuid")
    GrantPermissionsToUserCommand toCommand(UUID uuid, GrantPermissionsToUserRequest request);

    CreateRoleCommand toCommand(CreateRoleRequest request);

    @Mapping(target = "roleUuid", source = "uuid")
    UpdateRoleCommand toCommand(UUID uuid, UpdateRoleRequest request);

    @Mapping(target = "roleUuid", source = "uuid")
    AssignPermissionsToRoleCommand toCommand(UUID uuid, AssignPermissionsToRoleRequest request);

    CreatePermissionCommand toCommand(CreatePermissionRequest request);

    @Mapping(target = "permissionUuid", source = "uuid")
    UpdatePermissionCommand toCommand(UUID uuid, UpdatePermissionRequest request);

    @Mapping(target = "name", expression = "java(result.givenName() + \" \" + result.familyName())")
    @Mapping(target = "roles", expression = "java(java.util.List.of())")
    UserResponse toUserResponse(UserResult result);

    @Mapping(target = "name", expression = "java(result.givenName() + \" \" + result.familyName())")
    @Mapping(target = "roles", source = "authorities.roleDetails")
    UserResponse toUserResponse(UserResult result, UserAuthoritiesResult authorities);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<RoleDto> toRoleDtos(List<UserRoleResult> results);

    RoleDto toRoleDto(UserRoleResult result);

    IdentityResponse toIdentityResponse(IdentityResult result);

    @Mapping(target = "effectivePermissions", source = "permissions")
    UserAuthoritiesResponse toAuthoritiesResponse(UserAuthoritiesResult result);

    RoleSummaryResponse toRoleSummaryResponse(RoleSummary result);

    @Mapping(target = "isActive", source = "active")
    RoleResponse toRoleResponse(RoleResult result);

    @Mapping(target = "isActive", source = "active")
    PermissionResponse toPermissionResponse(PermissionResult result);

    @Mapping(target = "page", expression = "java(toPageMetadata(result))")
    PageResponse<RoleSummaryResponse> toRolePageResponse(PageResult<RoleSummary> result);

    PageResult<PermissionResponse> toPermissionPageResult(PageResult<PermissionResult> result);

    @Mapping(target = "number", source = "pageNumber")
    @Mapping(target = "size", source = "pageSize")
    PageMetadata toPageMetadata(PageResult<?> result);
}
