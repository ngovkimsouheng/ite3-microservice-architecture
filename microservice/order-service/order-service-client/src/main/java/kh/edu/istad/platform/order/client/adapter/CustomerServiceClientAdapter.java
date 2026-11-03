package kh.edu.istad.platform.order.client.adapter;

import kh.edu.istad.platform.order.client.exception.DownStreamServiceException;
import kh.edu.istad.platform.order.client.httpclient.CustomerHttpClient;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class CustomerServiceClientAdapter implements CustomerServiceClient {

//    private final WebClient.Builder webClientBuilder;
    private final CustomerHttpClient customerHttpClient;


    private record ValuePayload(String value) {}

    public record CustomerPayload(
            ValuePayload id,
            String username,
            String familyName,
            String givenName,
            ValuePayload email,
            ValuePayload phoneNumber,
            String status
    ) {}

    @Override
    public Flux<QueryCustomerResult> getCustomerName() {
        return customerHttpClient.queryCustomers()
                .onErrorMap(WebClientResponseException.Unauthorized.class, ex ->
                        new DownStreamServiceException("Access Denied: M2M token was rejected (401 Unauthorized) by customer-service.")
                )
                .onErrorMap(WebClientResponseException.Forbidden.class, ex ->
                        new DownStreamServiceException("Forbidden: Order service lacks the required scope/permissions (403 Forbidden).")
                )
                // Catch any other WebClient HTTP response error (like 500 Server Error)
                .onErrorMap(WebClientResponseException.class, ex ->
                        new DownStreamServiceException("Downstream error from customer-service [" + ex.getStatusCode() + "]: " + ex.getStatusText())
                );
    }

    @Override
    public Flux<CustomerResponse> getCustomers() {
//        return webClientBuilder
//                .baseUrl("http://customer-service")
//                .build()
//                .get()
//                .uri("/api/customers")
//                .retrieve()
//                .bodyToFlux(CustomerPayload.class)
//                .map(customer -> new CustomerResponse(
//                        customer.id().value(),
//                        customer.username(),
//                        customer.familyName(),
//                        customer.givenName(),
//                        customer.email().value(),
//                        customer.phoneNumber().value(),
//                        customer.status(),
//                        null
//                ));
//    }
        return null;}

}