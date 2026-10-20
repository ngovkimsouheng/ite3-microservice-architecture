package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.usecase.*;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateResponse;
import kh.edu.istad.platform.customer.restapi.dto.CustomerStatisticsResponse;
import kh.edu.istad.platform.customer.restapi.dto.UpdateCustomerRequest;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final InitiateCustomerUseCase initiateCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;
    private final QueryCustomerUseCase queryCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final FilterCustomersUseCase filterCustomersUseCase;
    private final GetCustomerStatisticsUseCase getCustomerStatisticsUseCase;

    @GetMapping
    public Flux<Customer> findAll() {
        return queryCustomerUseCase.findAll();
    }

    @GetMapping("/query")
    public Flux<Customer> query() {
        return queryCustomerUseCase.query();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<CustomerInitiateResponse> initiateCustomer(@Valid @RequestBody CustomerInitiateRequest request) {
        return initiateCustomerUseCase
                .execute(customerWebMapper.toCommand(request))
                .map(customerWebMapper::toResponse);
    }

    @GetMapping("/{id}")
    public Mono<Customer> findById(@PathVariable(name = "id") UUID id) {
        return queryCustomerUseCase.findById(new CustomerId(id));
    }

    @GetMapping("/username/{username}")
    public Mono<Customer> findByUsername(@PathVariable(name = "username") String username) {
        return queryCustomerUseCase.findByUsername(username);
    }

    @PutMapping("/{id}")
    public Mono<CustomerInitiateResponse> updateCustomer(
            @PathVariable(name = "id") UUID id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        return updateCustomerUseCase
                .execute(customerWebMapper.toCommand(id, request))
                .map(customerWebMapper::toResponse);
    }

    @PatchMapping("/{id}/deactivate")
    public Mono<Customer> deactivateCustomer(@PathVariable(name = "id") UUID id) {
        return deactivateCustomerUseCase.execute(new CustomerId(id));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCustomer(@PathVariable(name = "id") UUID id) {
        return deleteCustomerUseCase.execute(new CustomerId(id));
    }

    @GetMapping("/filter")
    public Flux<Customer> filterCustomers(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "givenName", required = false) String givenName,
            @RequestParam(name = "familyName", required = false) String familyName,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return filterCustomersUseCase.execute(username, status, givenName, familyName, page, size);
    }

    @GetMapping("/statistics")
    public Mono<CustomerStatisticsResponse> getStatistics() {
        return getCustomerStatisticsUseCase.execute()
                .map(res -> CustomerStatisticsResponse.builder()
                        .totalCustomers(res.getTotalCustomers())
                        .countByStatus(res.getCountByStatus())
                        .countBySegment(res.getCountBySegment())
                        .build());
    }
}