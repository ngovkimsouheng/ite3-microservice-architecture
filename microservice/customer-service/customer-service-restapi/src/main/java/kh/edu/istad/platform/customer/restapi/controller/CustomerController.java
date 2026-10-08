package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.usecase.DeactivateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.InitiateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.UpdateCustomerUseCase;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateResponse;
import kh.edu.istad.platform.customer.restapi.dto.UpdateCustomerRequest;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
    private final InitiateCustomerUseCase initiateCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CustomerInitiateResponse initiateCustomer(
            @RequestBody CustomerInitiateRequest customerInitiateRequest

    ) {
        InitiateCustomerResult result = initiateCustomerUseCase.
                execute(customerWebMapper.toCommand(customerInitiateRequest));
        return customerWebMapper.toResponse(result);
    }

    @PutMapping("/{customerId}")
    public Customer updateCustomer(
            @PathVariable("customerId") UUID customerId,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {

        UpdateCustomerCommand command = new UpdateCustomerCommand(
                new CustomerId(customerId),
                request.familyName(),
                request.givenName()
        );

        return updateCustomerUseCase.execute(command);
    }

    @PutMapping("/{customerId}/deactivate")
    public Customer deactivateCustomer(
            @PathVariable("customerId") UUID customerId
    ) {

        return deactivateCustomerUseCase.execute(
                new CustomerId(customerId)
        );
    }

}
