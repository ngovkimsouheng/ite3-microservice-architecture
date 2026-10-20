package kh.edu.istad.platform.customer.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatisticsResult;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository {
    Mono<Customer> save(Customer customer);
    Mono<Customer> findById(CustomerId customerId);
    Flux<Customer> findAll();
    Mono<Void> deleteById(CustomerId id);
    Mono<Customer> findByUsername(String username);
    Flux<Customer> query();
    Flux<Customer> filter(String username, String status, String givenName, String familyName, int page, int size);
    Mono<CustomerStatisticsResult> getStatistics();
}