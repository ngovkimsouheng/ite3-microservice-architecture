package kh.edu.istad.platform.order.domain.port.out.client;

import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import reactor.core.publisher.Flux;

public interface CustomerServiceClient {

    record CustomerResponse(
            String customerId,
            String username,
            String familyName,
            String givenName,
            String email,
            String phoneNumber,
            String customerStatus,
            String customerSegmentType
    ) {}

    Flux<QueryCustomerResult> getCustomerName();
    Flux<CustomerResponse> getCustomers();
}