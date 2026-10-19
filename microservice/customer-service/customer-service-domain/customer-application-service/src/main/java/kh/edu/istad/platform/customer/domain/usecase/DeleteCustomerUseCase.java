package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;

    public Mono<Void> execute(CustomerId customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(
                        new RuntimeException("Customer not found")
                ))
                .then(customerRepository.deleteById(customerId));
    }
}
