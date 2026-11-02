package kh.edu.istad.platform.order.domain.dto;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID customerId,
        UUID businessId,
        String street,
        String postalCode,
        String city,
        List<OrderItemCommand> items
) {
    public record OrderItemCommand(
            UUID productId,
            String productName,
            Integer quantity,
            java.math.BigDecimal price
    ) {
    }
}