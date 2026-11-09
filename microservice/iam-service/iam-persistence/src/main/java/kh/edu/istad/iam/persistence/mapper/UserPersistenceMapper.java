package kh.edu.istad.iam.persistence.mapper;

import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserPermissionAssignment;
import kh.edu.istad.iam.domain.core.entity.UserRoleAssignment;
import kh.edu.istad.iam.persistence.entity.UserJpaEntity;
import kh.edu.istad.iam.persistence.entity.UserPermissionAssignmentJpaEntity;
import kh.edu.istad.iam.persistence.entity.UserRoleAssignmentJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserPersistenceMapper {

    @Mapping(target = "version", defaultExpression = "java(0L)")
    @Mapping(target = "isDeleted", expression = "java(user.isDeleted())")
    UserJpaEntity toEntity(User user);

    @Mapping(target = "isDeleted", expression = "java(entity.isDeleted())")
    User toDomain(UserJpaEntity entity);

    UserRoleAssignmentJpaEntity toEntity(UserRoleAssignment assignment);

    UserRoleAssignment toDomain(UserRoleAssignmentJpaEntity entity);

    UserPermissionAssignmentJpaEntity toEntity(UserPermissionAssignment assignment);

    UserPermissionAssignment toDomain(UserPermissionAssignmentJpaEntity entity);
}
