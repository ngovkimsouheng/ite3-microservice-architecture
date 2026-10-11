package kh.edu.istad.platform.order.restapi.dto;

import kh.edu.istad.platform.order.domain.valueobject.OrderStatus;
import java.util.UUID;

public record OrderCreateResponse(
        UUID orderTrackingId,
        OrderStatus orderStatus,
        String message
) {}