package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.usecase.*;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateResponse;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import kh.edu.istad.common.domain.valueobject.CustomerId;

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
    public Mono<CustomerInitiateResponse> initiateCustomer(
            @Valid @RequestBody CustomerInitiateRequest request
    ) {
        return initiateCustomerUseCase
                .execute(customerWebMapper.toCommand(request))
                .map(customerWebMapper::toResponse);
    }

    @GetMapping("/{id}")
    public Mono<Customer> findById(
            @PathVariable("id") UUID id
    ) {
        return queryCustomerUseCase.findById(
                new CustomerId(id)
        );
    }

    @GetMapping("/username/{username}")
    public Mono<Customer> findByUsername(
            @PathVariable("username") String username
    ) {
        return queryCustomerUseCase.findByUsername(username);
    }

    @PatchMapping("/{id}/deactivate")
    public Mono<Customer> deactivateCustomer(
            @PathVariable("id") UUID id
    ) {
        return deactivateCustomerUseCase.execute(
                new CustomerId(id)
        );
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCustomer(@PathVariable("id") UUID id) {
        return deleteCustomerUseCase.execute(new CustomerId(id));
    }

}