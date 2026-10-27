package kh.edu.istad.platform.order.persistence.repository;

import kh.edu.istad.platform.order.persistence.entity.OrderItemEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface OrderItemReactiveRepository
        extends ReactiveCrudRepository<OrderItemEntity, Long> {

    Flux<OrderItemEntity> findAllByOrderId(UUID orderId);
}
