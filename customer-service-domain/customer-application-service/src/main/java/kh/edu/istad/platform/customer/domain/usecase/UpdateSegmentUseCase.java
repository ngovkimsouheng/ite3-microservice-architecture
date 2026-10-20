package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UpdateSegmentUseCase {
    private final CustomerSegmentRepository repository;

    public Mono<CustomerSegment> execute(String id, CustomerSegment updated) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setName(updated.getName());
                    existing.setDescription(updated.getDescription());
                    existing.setSegmentType(updated.getSegmentType());
                    existing.setModifiedAt(Instant.now());
                    return repository.save(existing);
                });
    }
}