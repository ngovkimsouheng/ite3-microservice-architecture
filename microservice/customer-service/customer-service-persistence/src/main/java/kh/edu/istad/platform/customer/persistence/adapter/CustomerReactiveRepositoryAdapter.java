package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import kh.edu.istad.platform.customer.persistence.mapper.CustomerPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Repository
public class CustomerReactiveRepositoryAdapter implements CustomerRepository {

    private final CustomerMongoRepository customerMongoRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<Customer> save(Customer customer) {
        CustomerDocument document =
                customerPersistenceMapper.toDocument(customer);

        return customerMongoRepository.save(document)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Customer> findById(CustomerId customerId) {
        return customerMongoRepository.findById(customerId.value().toString())
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Customer> findAll() {
        return customerMongoRepository.findAll()
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(CustomerId id) {
        return customerMongoRepository.deleteById(id.value().toString());
    }

    @Override
    public Mono<Customer> findByUsername(String username) {
        return customerMongoRepository.findByUsername(username)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Customer> query() {

        Query query = Query.query(Criteria.where("username").is("long"));

        return reactiveMongoTemplate.find(query, CustomerDocument.class).map(
                customerPersistenceMapper::toDomain
        );
    }
}