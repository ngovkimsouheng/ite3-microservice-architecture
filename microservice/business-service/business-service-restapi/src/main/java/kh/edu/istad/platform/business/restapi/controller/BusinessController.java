package kh.edu.istad.platform.business.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.ValidateOrderCommand;
import kh.edu.istad.platform.business.domain.port.in.CreateBusinessUseCase;
import kh.edu.istad.platform.business.domain.port.in.ValidateOrderUseCase;
import kh.edu.istad.platform.business.restapi.dto.CreateBusinessRequest;
import kh.edu.istad.platform.business.restapi.dto.CreateBusinessResponse;
import kh.edu.istad.platform.business.restapi.dto.ValidateOrderRequest;
import kh.edu.istad.platform.business.restapi.dto.ValidateOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final CreateBusinessUseCase createBusinessUseCase;
    private final ValidateOrderUseCase validateOrderUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreateBusinessResponse> createBusiness(@Valid @RequestBody CreateBusinessRequest request) {
        CreateBusinessCommand command = new CreateBusinessCommand(
                request.legalName(),
                request.displayName(),
                request.email(),
                request.phoneNumber(),
                request.active(),
                request.products().stream()
                        .map(p -> new CreateBusinessCommand.ProductItem(
                                p.id(),
                                p.name(),
                                p.price(),
                                p.quantity(),
                                p.available()
                        ))
                        .toList()
        );

        return createBusinessUseCase.execute(command)
                .map(result -> new CreateBusinessResponse(
                        result.businessId(),
                        result.legalName(),
                        result.displayName(),
                        result.active(),
                        result.products().stream()
                                .map(p -> new CreateBusinessResponse.ProductResponse(
                                        p.id(),
                                        p.name(),
                                        p.price(),
                                        p.quantity(),
                                        p.available()
                                ))
                                .toList(),
                        result.message()
                ));
    }

    @PostMapping("/orders/approval")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ValidateOrderResponse> validateOrderApproval(@Valid @RequestBody ValidateOrderRequest request) {
        ValidateOrderCommand command = new ValidateOrderCommand(
                request.businessId(),
                request.orderId(),
                request.totalAmount(),
                request.products() != null ? request.products().stream()
                        .map(p -> new ValidateOrderCommand.ProductItem(p.id(), p.quantity()))
                        .toList() : List.of()
        );

        return validateOrderUseCase.execute(command)
                .map(result -> new ValidateOrderResponse(
                        result.orderApprovalId(),
                        result.orderId(),
                        result.approvalStatus(),
                        result.failureMessages()
                ));
    }
}
