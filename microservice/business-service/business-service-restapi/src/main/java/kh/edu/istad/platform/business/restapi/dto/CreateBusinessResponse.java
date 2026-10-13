package kh.edu.istad.platform.business.restapi.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateBusinessResponse(
    UUID businessId,
    String name,
    boolean active,
    List<ProductResponse> products,
    String message
) {
    public record ProductResponse(
        UUID id,
        String name,
        BigDecimal price,
        boolean available
    ) {}
}
