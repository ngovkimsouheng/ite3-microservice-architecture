package kh.edu.istad.platform.business.restapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateBusinessRequest(
    @NotBlank(message = "Business name is required")
    String name,

    Boolean active,

    @NotEmpty(message = "At least one product is required")
    @Valid
    List<ProductRequest> products
) {
    public record ProductRequest(
        UUID id,

        @NotBlank(message = "Product name is required")
        String name,

        @NotNull(message = "Product price is required")
        @Positive(message = "Product price must be greater than zero")
        BigDecimal price,

        Boolean available
    ) {}
}
