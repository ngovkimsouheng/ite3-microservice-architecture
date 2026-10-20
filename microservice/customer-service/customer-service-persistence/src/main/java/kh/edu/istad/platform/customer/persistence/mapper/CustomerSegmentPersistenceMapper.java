package kh.edu.istad.platform.customer.persistence.mapper;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.persistence.document.CustomerSegmentDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerSegmentPersistenceMapper {

    @Mapping(source = "id.value", target = "id")
    CustomerSegmentDocument toDocument(CustomerSegment segment);

    @Mapping(source = "id", target = "id")
    CustomerSegment toDomain(CustomerSegmentDocument document);

    default CustomerSegmentId toCustomerSegmentId(String id) {
        return id != null ? new CustomerSegmentId(id) : null;
    }

    default String fromCustomerSegmentId(CustomerSegmentId id) {
        return id != null ? id.getValue() : null;
    }
}