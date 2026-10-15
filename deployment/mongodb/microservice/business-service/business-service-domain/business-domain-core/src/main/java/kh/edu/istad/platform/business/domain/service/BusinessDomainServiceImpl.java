package kh.edu.istad.platform.business.domain.service;

import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.event.OrderApprovalEvent;
import kh.edu.istad.platform.business.domain.event.OrderApprovedEvent;
import kh.edu.istad.platform.business.domain.event.OrderRejectedEvent;
import kh.edu.istad.platform.business.domain.valueobject.OrderApprovalStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class BusinessDomainServiceImpl implements BusinessDomainService {

    private static final String UTC = "UTC";

    @Override
    public OrderApprovalEvent validateOrder(Business business, List<String> failureMessages) {
        business.validateOrder(failureMessages);

        if (failureMessages.isEmpty()) {
            business.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(
                    business.getOrderApproval(),
                    business.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC))
            );
        } else {
            business.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return new OrderRejectedEvent(
                    business.getOrderApproval(),
                    business.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC))
            );
        }
    }
}
