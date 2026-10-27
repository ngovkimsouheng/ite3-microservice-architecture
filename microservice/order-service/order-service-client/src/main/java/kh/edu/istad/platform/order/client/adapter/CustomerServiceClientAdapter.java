package kh.edu.istad.platform.order.client.adapter;

import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class CustomerServiceClientAdapter implements CustomerServiceClient {

    private final WebClient.Builder webClientBuilder;

    public record GivenName(String givenName) {

    }

    private record ValuePayload(String value) {
    }

    private record CustomerPayload(
            ValuePayload id,
            String username,
            String familyName,
            String givenName,
            ValuePayload email,
            ValuePayload phoneNumber,
            String status
    ) {
    }

    @Override
    public Flux<CustomerResponse> getCustomerNames() {
        return webClientBuilder
                .baseUrl("http://customer-service")
                .build()
                .get()
                .uri("/api/customers")
                .retrieve()
                .bodyToFlux(CustomerPayload.class)
                .map(customer -> new CustomerResponse(
                        customer.id().value(),
                        customer.username(),
                        customer.familyName(),
                        customer.givenName(),
                        customer.email().value(),
                        customer.phoneNumber().value(),
                        customer.status(),
                        null
                ));
    }
}