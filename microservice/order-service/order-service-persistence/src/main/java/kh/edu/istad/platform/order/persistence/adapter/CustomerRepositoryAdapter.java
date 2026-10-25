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

        log.info("Querying Customer Service: {}", targetUri);

        return loadBalancedWebClient.get()
                .uri(targetUri)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.releaseBody()
                                .thenReturn(new Customer(customerId));
                    }

                    if (response.statusCode().value() == 404) {
                        return response.releaseBody()
                                .then(Mono.empty());
                    }

                    return response.createException()
                            .flatMap(Mono::error);
                })
                .doOnError(ex -> log.error(
                        "Failed to reach Customer Service: {}",
                        ex.getMessage()
                ));
    }
}