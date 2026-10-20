package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.dto.CustomerSegmentResult;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class QueryCustomerSegmentUseCase {

    private final CustomerSegmentRepository customerSegmentRepository;

    public Flux<CustomerSegmentResult> findAll() {
        return customerSegmentRepository.findAll()
                .map(seg -> new CustomerSegmentResult(
                        seg.getId().value(),
                        seg.getName(),
                        seg.getDescription(),
                        seg.getSegmentType(),
                        seg.getCreatedAt(),
                        seg.getModifiedAt()
                ));
    }

    public Mono<CustomerSegmentResult> findById(CustomerSegmentId id) {
        return customerSegmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerDomainException("Customer segment not found: " + id.value())))
                .map(seg -> new CustomerSegmentResult(
                        seg.getId().value(),
                        seg.getName(),
                        seg.getDescription(),
                        seg.getSegmentType(),
                        seg.getCreatedAt(),
                        seg.getModifiedAt()
                ));
    }
}