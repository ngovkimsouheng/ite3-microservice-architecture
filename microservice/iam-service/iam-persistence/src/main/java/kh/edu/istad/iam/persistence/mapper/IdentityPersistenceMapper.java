package kh.edu.istad.iam.persistence.mapper;

import kh.edu.istad.iam.domain.core.entity.Identity;
import kh.edu.istad.iam.persistence.entity.IdentityJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IdentityPersistenceMapper {

    @Mapping(target = "version", defaultExpression = "java(0L)")
    @Mapping(target = "isDeleted", expression = "java(identity.isDeleted())")
    IdentityJpaEntity toEntity(Identity identity);

    @Mapping(target = "isDeleted", expression = "java(entity.isDeleted())")
    Identity toDomain(IdentityJpaEntity entity);
}
