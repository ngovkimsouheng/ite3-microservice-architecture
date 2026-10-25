package kh.edu.istad.platform.order.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.usecase.CancelOrderUseCase;
import kh.edu.istad.platform.order.domain.usecase.CreateOrderUseCase;
import kh.edu.istad.platform.order.domain.usecase.PayOrderUseCase;
//import kh.edu.istad.platform.order.restapi.dto.CreateOrderResponse;
import kh.edu.istad.platform.order.restapi.dto.OrderCreateRequest;
import kh.edu.istad.platform.order.restapi.dto.OrderCreateResponse;
import kh.edu.istad.platform.order.restapi.mapper.OrderWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final OrderWebMapper orderWebMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<OrderCreateResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request) {

        return createOrderUseCase
                .execute(orderWebMapper.toCommand(request))
                .map(orderWebMapper::toResponse);
    }

    @PutMapping("/{orderId}/pay")
    public Order payOrder(@PathVariable("orderId") UUID orderId) {
        return payOrderUseCase.execute(orderId);
    }

    @PutMapping("/{orderId}/cancel")
    public Order cancelOrder(@PathVariable("orderId") UUID orderId) {
        return cancelOrderUseCase.execute(orderId);
    }
}