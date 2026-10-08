package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeactivateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public Customer execute(CustomerId customerId) {

        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found")
                );

        customer.deactivateCustomer();

        return customerRepository.save(customer);
    }
}