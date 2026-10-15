package kh.edu.istad.platform.business.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ValidateOrderCommand(
        UUID businessId,
        UUID orderId,
        BigDecimal totalAmount,
        List<ProductItem> products
) {
    public record ProductItem(
            UUID id,
            Integer quantity
    ) {}
}
