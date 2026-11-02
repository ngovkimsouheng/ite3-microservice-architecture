package kh.edu.istad.platform.order.restapi.controller;

import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.usecase.CreateOrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping
    public Mono<ResponseEntity<CreateOrderResult>> createOrder(
            @RequestBody CreateOrderCommand command
    ) {
        return createOrderUseCase.execute(command)
                .map(result ->
                        ResponseEntity.status(HttpStatus.CREATED)
                                .body(result)
                );
    }
}