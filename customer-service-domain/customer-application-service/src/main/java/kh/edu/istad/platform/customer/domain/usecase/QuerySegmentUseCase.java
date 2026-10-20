package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QuerySegmentUseCase {
    private final CustomerSegmentRepository repository;

    public Flux<CustomerSegment> findAll() {
        return repository.findAll();
    }

    public Mono<CustomerSegment> findById(String id) {
        return repository.findById(id);
    }
}