package kh.edu.istad.platform.business.restapi.dto;

import kh.edu.istad.platform.business.domain.valueobject.OrderApprovalStatus;
import java.util.List;
import java.util.UUID;

public record ValidateOrderResponse(
        UUID orderApprovalId,
        UUID orderId,
        OrderApprovalStatus approvalStatus,
        List<String> failureMessages
) {}
