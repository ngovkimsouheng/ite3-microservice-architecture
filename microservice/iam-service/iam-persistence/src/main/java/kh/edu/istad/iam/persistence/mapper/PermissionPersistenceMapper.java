package kh.edu.istad.iam.persistence.mapper;

import kh.edu.istad.iam.domain.core.entity.Permission;
import kh.edu.istad.iam.persistence.entity.PermissionJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PermissionPersistenceMapper {

    @Mapping(target = "version", defaultExpression = "java(0L)")
    @Mapping(target = "isSystem", expression = "java(permission.isSystem())")
    @Mapping(target = "isActive", expression = "java(permission.isActive())")
    @Mapping(target = "isDeleted", expression = "java(permission.isDeleted())")
    PermissionJpaEntity toEntity(Permission permission);

    @Mapping(target = "isSystem", expression = "java(entity.isSystem())")
    @Mapping(target = "isActive", expression = "java(entity.isActive())")
    @Mapping(target = "isDeleted", expression = "java(entity.isDeleted())")
    Permission toDomain(PermissionJpaEntity entity);
}
