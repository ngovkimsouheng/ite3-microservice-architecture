package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import kh.edu.istad.platform.order.domain.valueobject.OrderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;

    public Mono<Order> execute(UUID orderId) {
        log.info("Cancelling order with id: {}", orderId);

        return orderRepository.findById(new OrderId(orderId))
                .switchIfEmpty(Mono.error(
                        new OrderDomainException(
                                "Order not found with id: " + orderId
                        )
                ))
                .flatMap(order -> {
                    orderDomainService.cancelOrder(
                            order,
                            List.of("Cancelled by user request")
                    );
                    return orderRepository.save(order);
                });
    }
}
