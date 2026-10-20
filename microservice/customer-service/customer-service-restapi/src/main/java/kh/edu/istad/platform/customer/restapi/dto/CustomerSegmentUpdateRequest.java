package kh.edu.istad.platform.customer.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;

public record CustomerSegmentUpdateRequest(
        @NotBlank(message = "Segment name is required")
        String name,

        String description,

        @NotNull(message = "Segment type is required (SILVER, GOLD, PREMIUM)")
        CustomerSegmentType segmentType
) {}