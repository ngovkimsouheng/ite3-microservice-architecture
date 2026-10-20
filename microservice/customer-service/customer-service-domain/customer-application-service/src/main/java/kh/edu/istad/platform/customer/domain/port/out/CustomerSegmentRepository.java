package kh.edu.istad.platform.customer.domain.port.out;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerSegmentRepository {
    Mono<CustomerSegment> save(CustomerSegment segment);
    Mono<CustomerSegment> findById(CustomerSegmentId id);
    Flux<CustomerSegment> findAll();
    Mono<Void> deleteById(CustomerSegmentId id);
}