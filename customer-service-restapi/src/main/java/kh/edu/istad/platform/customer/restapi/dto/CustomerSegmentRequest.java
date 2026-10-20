package kh.edu.istad.platform.customer.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import lombok.Data;

@Data
public class CustomerSegmentRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private CustomerSegmentType segmentType;
}