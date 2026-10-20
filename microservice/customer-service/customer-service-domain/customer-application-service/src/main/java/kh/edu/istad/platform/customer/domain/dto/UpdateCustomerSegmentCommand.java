package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;

public record UpdateCustomerSegmentCommand(
        String id,
        String name,
        String description,
        CustomerSegmentType segmentType
) {}