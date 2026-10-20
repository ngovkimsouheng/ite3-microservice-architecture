package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.customer.domain.usecase.*;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.restapi.dto.CustomerSegmentCreateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerSegmentResponse;
import kh.edu.istad.platform.customer.restapi.dto.CustomerSegmentUpdateRequest;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerSegmentWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/segments")
public class CustomerSegmentController {

    private final CreateCustomerSegmentUseCase createCustomerSegmentUseCase;
    private final UpdateCustomerSegmentUseCase updateCustomerSegmentUseCase;
    private final QueryCustomerSegmentUseCase queryCustomerSegmentUseCase;
    private final DeleteCustomerSegmentUseCase deleteCustomerSegmentUseCase;
    private final CustomerSegmentWebMapper customerSegmentWebMapper;

    @GetMapping
    public Flux<CustomerSegmentResponse> findAll() {
        return queryCustomerSegmentUseCase.findAll()
                .map(customerSegmentWebMapper::toResponse);
    }

    @GetMapping("/{id}")
    public Mono<CustomerSegmentResponse> findById(@PathVariable(name = "id") String id) {
        return queryCustomerSegmentUseCase.findById(new CustomerSegmentId(id))
                .map(customerSegmentWebMapper::toResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomerSegmentResponse> create(
            @Valid @RequestBody CustomerSegmentCreateRequest request
    ) {
        return createCustomerSegmentUseCase
                .execute(customerSegmentWebMapper.toCommand(request))
                .map(customerSegmentWebMapper::toResponse);
    }

    @PutMapping("/{id}")
    public Mono<CustomerSegmentResponse> update(
            @PathVariable(name = "id") String id,
            @Valid @RequestBody CustomerSegmentUpdateRequest request
    ) {
        return updateCustomerSegmentUseCase
                .execute(customerSegmentWebMapper.toCommand(id, request))
                .map(customerSegmentWebMapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable(name = "id") String id) {
        return deleteCustomerSegmentUseCase.execute(new CustomerSegmentId(id));
    }
}