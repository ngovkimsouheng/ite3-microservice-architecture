package kh.edu.istad.platform.business.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateBusinessResult(
        UUID businessId,
        String legalName,
        String displayName,
        boolean active,
        List<ProductItemResult> products,
        String message
) {
    public record ProductItemResult(
            UUID id,
            String name,
            BigDecimal price,
            int quantity,
            boolean available
    ) {}
}
