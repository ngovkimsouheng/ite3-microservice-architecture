package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.platform.business.domain.valueobject.OrderApprovalId;
import kh.edu.istad.platform.business.domain.valueobject.OrderApprovalStatus;

import java.util.UUID;

public class OrderApproval extends BaseEntity<OrderApprovalId> {
    private final BusinessId businessId;
    private final UUID orderId;
    private final OrderApprovalStatus approvalStatus;

    public OrderApproval(OrderApprovalId orderApprovalId,
                         BusinessId businessId,
                         UUID orderId,
                         OrderApprovalStatus approvalStatus) {
        super.setId(orderApprovalId);
        this.businessId = businessId;
        this.orderId = orderId;
        this.approvalStatus = approvalStatus;
    }

    public BusinessId getBusinessId() { return businessId; }
    public UUID getOrderId() { return orderId; }
    public OrderApprovalStatus getApprovalStatus() { return approvalStatus; }
}
