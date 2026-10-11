package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.platform.order.domain.valueobject.OrderStatus;
import java.util.UUID;

public record CreateOrderResult(
        UUID orderTrackingId,
        OrderStatus orderStatus,
        String message
) {}