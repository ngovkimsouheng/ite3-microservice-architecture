package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeleteSegmentUseCase {
    private final CustomerSegmentRepository repository;

    public Mono<Void> execute(String id) {
        return repository.deleteById(id);
    }
}