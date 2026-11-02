package kh.edu.istad.platform.order.persistence.repository;

import kh.edu.istad.platform.order.persistence.entity.OrderItemEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderItemEntityId;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

public interface OrderItemReactiveRepository extends R2dbcRepository<OrderItemEntity, OrderItemEntityId> {

    Flux<OrderItemEntity> findAllByIdOrderId(UUID orderId);
}