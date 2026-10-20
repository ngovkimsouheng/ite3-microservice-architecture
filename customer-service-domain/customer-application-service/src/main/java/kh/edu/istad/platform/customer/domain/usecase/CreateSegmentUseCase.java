package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateSegmentUseCase {
    private final CustomerSegmentRepository repository;

    public Mono<CustomerSegment> execute(CustomerSegment segment) {
        if (segment.getId() == null || segment.getId().isBlank()) {
            segment.setId(UUID.randomUUID().toString());
        }
        segment.setCreatedAt(Instant.now());
        segment.setModifiedAt(Instant.now());
        return repository.save(segment);
    }
}