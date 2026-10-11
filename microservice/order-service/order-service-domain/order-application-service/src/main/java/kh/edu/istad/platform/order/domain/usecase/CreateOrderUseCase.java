package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import kh.edu.istad.platform.order.domain.valueobject.ProductId;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;

    public CreateOrderResult execute(CreateOrderCommand command) {
        log.info("CreateOrderUseCase.execute: command = {}", command);

        List<OrderItem> orderItems = command.items().stream()
                .map(item -> OrderItem.Builder.builder()
                        .product(new Product(new ProductId(item.productId()), "", new Money(item.price())))
                        .quantity(item.quantity())
                        .price(new Money(item.price()))
                        .subTotal(new Money(item.subTotal()))
                        .build())
                .toList();

        StreetAddress deliveryAddress = new StreetAddress(
                UUID.randomUUID(),
                command.street(),
                command.postalCode(),
                command.city()
        );

        Order order = Order.Builder.builder()
                .customerId(new CustomerId(command.customerId()))
                .businessId(new BusinessId(command.businessId()))
                .deliveryAddress(deliveryAddress)
                .price(new Money(command.price()))
                .items(orderItems)
                .build();

        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order);

        Order savedOrder = orderRepository.save(orderCreatedEvent.order());
        log.info("Order saved with ID: {}", savedOrder.getId().value());

        return new CreateOrderResult(
                savedOrder.getTrackingId().value(),
                savedOrder.getOrderStatus(),
                "Order created successfully"
        );
    }
}