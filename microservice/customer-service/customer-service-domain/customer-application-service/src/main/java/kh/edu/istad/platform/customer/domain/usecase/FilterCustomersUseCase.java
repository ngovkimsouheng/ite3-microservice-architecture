package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class FilterCustomersUseCase {

    private final CustomerRepository customerRepository;

    public Flux<Customer> execute(
            String username,
            String status,
            String givenName,
            String familyName,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        return customerRepository.filter(
                username, status, givenName, familyName, safePage, safeSize
        );
    }
}
