package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.order.domain.entity.Customer;
import kh.edu.istad.platform.order.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.order.persistence.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpConnectTimeoutException;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    // HARD-CODED address to customer-service (Part 1 Requirement)
    private static final String CUSTOMER_SERVICE_URL = "http://localhost:9091/api/customers";

    @Override
    public Optional<Customer> findCustomer(CustomerId customerId) {
        String targetUrl = CUSTOMER_SERVICE_URL + "/" + customerId.value();
        log.info("Sending HTTP GET request to hard-coded address: {}", targetUrl);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Successfully received customer response: {}", response.body());
                return Optional.of(new Customer(customerId));
            } else {
                log.warn("Customer service returned non-2xx status code: {}", response.statusCode());
                return Optional.empty();
            }
        } catch (ConnectException ex) {
            // Explicit error log for connection failure (Port down / Target unavailable)
            log.error("CONNECTION FAILED: Target hard-coded server is unreachable at [{}]. Cause: {}",
                    targetUrl, ex.getMessage(), ex);
            return Optional.empty();
        } catch (HttpConnectTimeoutException ex) {
            // Explicit error log for timeout
            log.error("CONNECTION TIMEOUT: Customer service at [{}] took too long to respond. Cause: {}",
                    targetUrl, ex.getMessage(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            // General failure log
            log.error("FAILED to communicate with hard-coded customer service at [{}]. Exception: {}",
                    targetUrl, ex.getClass().getSimpleName(), ex);
            return Optional.empty();
        }
    }
}