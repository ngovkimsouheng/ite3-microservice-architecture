package kh.edu.istad.platform.customer.persistence.mapper;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.persistence.document.CustomerSegmentDocument;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerSegmentPersistenceMapper {
    CustomerSegmentDocument toDocument(CustomerSegment domain);
    CustomerSegment toDomain(CustomerSegmentDocument doc);
}