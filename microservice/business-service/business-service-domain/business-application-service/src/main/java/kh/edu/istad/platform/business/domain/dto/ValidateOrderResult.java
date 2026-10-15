package kh.edu.istad.platform.business.domain.dto;

import kh.edu.istad.platform.business.domain.valueobject.OrderApprovalStatus;

import java.util.List;
import java.util.UUID;

public record ValidateOrderResult(
        UUID orderApprovalId,
        UUID orderId,
        OrderApprovalStatus approvalStatus,
        List<String> failureMessages
) {}
