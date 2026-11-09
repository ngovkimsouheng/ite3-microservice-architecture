package kh.edu.istad.iam.persistence.mapper;

import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.RolePermissionAssignment;
import kh.edu.istad.iam.persistence.entity.RoleJpaEntity;
import kh.edu.istad.iam.persistence.entity.RolePermissionAssignmentJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RolePersistenceMapper {

    @Mapping(target = "version", defaultExpression = "java(0L)")
    @Mapping(target = "isSystem", expression = "java(role.isSystem())")
    @Mapping(target = "isActive", expression = "java(role.isActive())")
    @Mapping(target = "isDeleted", expression = "java(role.isDeleted())")
    RoleJpaEntity toEntity(Role role);

    @Mapping(target = "isSystem", expression = "java(entity.isSystem())")
    @Mapping(target = "isActive", expression = "java(entity.isActive())")
    @Mapping(target = "isDeleted", expression = "java(entity.isDeleted())")
    Role toDomain(RoleJpaEntity entity);

    RolePermissionAssignmentJpaEntity toEntity(RolePermissionAssignment assignment);

    RolePermissionAssignment toDomain(RolePermissionAssignmentJpaEntity entity);
}
