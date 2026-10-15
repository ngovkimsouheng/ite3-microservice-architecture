package kh.edu.istad.platform.business.persistence.repository;

import kh.edu.istad.platform.business.persistence.document.BusinessDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface BusinessReactiveMongoRepository extends ReactiveMongoRepository<BusinessDocument, UUID> {
    Mono<Boolean> existsByLegalName(String legalName);
}
