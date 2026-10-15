package kh.edu.istad.platform.business.restapi.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ValidateOrderRequest(
        @NotNull UUID businessId,
        @NotNull UUID orderId,
        @NotNull BigDecimal totalAmount,
        List<ProductItem> products
) {
    public record ProductItem(
            @NotNull UUID id,
            @NotNull Integer quantity
    ) {}
}
