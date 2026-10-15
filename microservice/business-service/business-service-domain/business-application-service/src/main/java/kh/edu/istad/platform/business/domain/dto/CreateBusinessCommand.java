package kh.edu.istad.platform.business.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateBusinessCommand(
        String legalName,
        String displayName,
        String email,
        String phoneNumber,
        Boolean active,
        List<ProductItem> products
) {
    public record ProductItem(
            UUID id,
            String name,
            BigDecimal price,
            Integer quantity,
            Boolean available
    ) {}
}
