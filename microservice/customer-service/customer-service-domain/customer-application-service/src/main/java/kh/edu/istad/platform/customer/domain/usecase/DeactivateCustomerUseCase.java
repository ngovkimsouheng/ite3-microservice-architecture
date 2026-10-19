package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DeactivateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public Mono<Customer> execute(CustomerId customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(
                        new RuntimeException("Customer not found")
                ))
                .flatMap(customer -> {
                    customer.deactivateCustomer();
                    return customerRepository.save(customer);
                });
    }
}