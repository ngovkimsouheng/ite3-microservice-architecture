package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.order.domain.entity.Customer;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findCustomer(CustomerId customerId);
}