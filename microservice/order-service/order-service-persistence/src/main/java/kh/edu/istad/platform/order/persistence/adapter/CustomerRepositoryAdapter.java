package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.order.domain.entity.Customer;
import kh.edu.istad.platform.order.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final WebClient loadBalancedWebClient;
    private static final String CUSTOMER_SERVICE_URI = "http://customer-service/api/customers";

    @Override
    public Mono<Customer> findCustomer(CustomerId customerId) {
        String targetUri = CUSTOMER_SERVICE_URI + "/" + customerId.value();
        log.info("[EUREKA DISCOVERY] Querying Customer Service via virtual URI: {}", targetUri);

        return loadBalancedWebClient.get()
                .uri(targetUri)
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return Mono.just(new Customer(customerId));
                    }
                    return Mono.empty();
                })
                .onErrorResume(ex -> {
                    log.error("Failed to reach customer-service via Eureka at [{}]: {}", targetUri, ex.getMessage());
                    return Mono.empty();
                });
    }
}