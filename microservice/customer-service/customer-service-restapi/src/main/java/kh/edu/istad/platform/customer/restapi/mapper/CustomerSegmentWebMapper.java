package kh.edu.istad.platform.customer.restapi.mapper;

import kh.edu.istad.platform.customer.domain.dto.CreateCustomerSegmentCommand;
import kh.edu.istad.platform.customer.domain.dto.CustomerSegmentResult;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerSegmentCommand;
import kh.edu.istad.platform.customer.restapi.dto.CustomerSegmentCreateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerSegmentResponse;
import kh.edu.istad.platform.customer.restapi.dto.CustomerSegmentUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerSegmentWebMapper {

    CreateCustomerSegmentCommand toCommand(CustomerSegmentCreateRequest request);

    default UpdateCustomerSegmentCommand toCommand(String id, CustomerSegmentUpdateRequest request) {
        return new UpdateCustomerSegmentCommand(id, request.name(), request.description(), request.segmentType());
    }

    CustomerSegmentResponse toResponse(CustomerSegmentResult result);
}