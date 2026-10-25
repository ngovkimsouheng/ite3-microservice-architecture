package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.order.domain.entity.Customer;
import reactor.core.publisher.Mono;

public interface CustomerRepository {
    Mono<Customer> findCustomer(CustomerId customerId);
}