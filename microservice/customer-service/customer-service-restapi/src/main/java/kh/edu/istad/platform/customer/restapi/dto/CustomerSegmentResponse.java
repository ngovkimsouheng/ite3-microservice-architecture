package kh.edu.istad.platform.customer.restapi.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import java.time.Instant;

public record CustomerSegmentResponse(
        String id,
        String name,
        String description,
        CustomerSegmentType segmentType,
        Instant createdAt,
        Instant modifiedAt
) {}