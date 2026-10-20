package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.persistence.mapper.CustomerSegmentPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerSegmentMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomerSegmentReactiveRepositoryAdapter implements CustomerSegmentRepository {

    private final CustomerSegmentMongoRepository repository;
    private final CustomerSegmentPersistenceMapper mapper;

    @Override
    public Mono<CustomerSegment> save(CustomerSegment segment) {
        return repository.save(mapper.toDocument(segment))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<CustomerSegment> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<CustomerSegment> findAll() {
        return repository.findAll().map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}