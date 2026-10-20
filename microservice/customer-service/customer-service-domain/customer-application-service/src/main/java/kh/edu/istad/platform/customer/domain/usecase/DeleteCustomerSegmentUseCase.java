package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DeleteCustomerSegmentUseCase {

    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<Void> execute(CustomerSegmentId id) {
        return customerSegmentRepository.deleteById(id);
    }
}