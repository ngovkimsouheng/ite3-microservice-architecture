package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.common.domain.valueobject.CustomerId;

public record UpdateCustomerCommand(
        CustomerId customerId,
        String familyName,
        String givenName
) {
}