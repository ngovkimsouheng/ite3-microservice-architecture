package kh.edu.istad.platform.order.client.httpclient;

import kh.edu.istad.platform.order.client.adapter.CustomerServiceClientAdapter;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@HttpExchange("/api/customers")
@ClientRegistrationId("customer-client")
public interface CustomerHttpClient {

    @GetExchange
    Flux<QueryCustomerResult> queryCustomers();

    @GetExchange("/{id}")
    Mono<CustomerServiceClientAdapter.CustomerPayload> getCustomerById(@PathVariable("id") String id);

    @GetExchange("/{id}")
    Mono<QueryCustomerResult> queryCustomerById(@PathVariable String id);

    @PostExchange
    Mono<Void> createCustomer (@RequestBody QueryCustomerResult query);
}