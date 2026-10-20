package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatisticsResult;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import kh.edu.istad.platform.customer.persistence.mapper.CustomerPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerMongoRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Repository
public class CustomerReactiveRepositoryAdapter implements CustomerRepository {

    private final CustomerMongoRepository customerMongoRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<Customer> save(Customer customer) {
        CustomerDocument document = customerPersistenceMapper.toDocument(customer);

        return customerMongoRepository.existsById(document.getId())
                .flatMap(exists -> {
                    if (exists) {
                        document.setNew(false);
                        return customerMongoRepository.save(document);
                    } else {
                        document.setNew(true);
                        return customerMongoRepository.insert(document);
                    }
                })
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
        return reactiveMongoTemplate.find(query, CustomerDocument.class)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Customer> filter(
            String username,
            String status,
            String givenName,
            String familyName,
            int page,
            int size
    ) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (username != null && !username.isBlank()) {
            criteriaList.add(Criteria.where("username").regex(Pattern.quote(username.trim()), "i"));
        }
        if (status != null && !status.isBlank()) {
            criteriaList.add(Criteria.where("status").is(status.trim().toUpperCase()));
        }
        if (givenName != null && !givenName.isBlank()) {
            criteriaList.add(Criteria.where("givenName").regex(Pattern.quote(givenName.trim()), "i"));
        }
        if (familyName != null && !familyName.isBlank()) {
            criteriaList.add(Criteria.where("familyName").regex(Pattern.quote(familyName.trim()), "i"));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        query.skip((long) page * size);
        query.limit(size);

        return reactiveMongoTemplate.find(query, CustomerDocument.class)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<CustomerStatisticsResult> getStatistics() {
        Aggregation aggregation = newAggregation(
                facet(count().as("total")).as("totalCustomersFacet")
                .and(group("status").count().as("count")).as("statusFacet")
                .and(group("segment").count().as("count")).as("segmentFacet")
        );

        return reactiveMongoTemplate.aggregate(aggregation, "customerDocument", Document.class)
                .next()
                .map(doc -> {
                    long total = 0L;
                    List<Document> totalList = doc.getList("totalCustomersFacet", Document.class);
                    if (totalList != null && !totalList.isEmpty()) {
                        Number n = (Number) totalList.get(0).get("total");
                        total = n != null ? n.longValue() : 0L;
                    }

                    Map<String, Long> statusMap = new HashMap<>();
                    List<Document> statusList = doc.getList("statusFacet", Document.class);
                    if (statusList != null) {
                        for (Document d : statusList) {
                            String k = d.getString("_id");
                            Number c = (Number) d.get("count");
                            if (k != null) statusMap.put(k, c != null ? c.longValue() : 0L);
                        }
                    }

                    Map<String, Long> segmentMap = new HashMap<>();
                    List<Document> segmentList = doc.getList("segmentFacet", Document.class);
                    if (segmentList != null) {
                        for (Document d : segmentList) {
                            String k = d.getString("_id");
                            Number c = (Number) d.get("count");
                            if (k != null) segmentMap.put(k, c != null ? c.longValue() : 0L);
                        }
                    }

                    return CustomerStatisticsResult.builder()
                            .totalCustomers(total)
                            .countByStatus(statusMap)
                            .countBySegment(segmentMap)
                            .build();
                })
                .defaultIfEmpty(CustomerStatisticsResult.builder()
                        .totalCustomers(0L)
                        .countByStatus(Collections.emptyMap())
                        .countBySegment(Collections.emptyMap())
                        .build());
    }
}