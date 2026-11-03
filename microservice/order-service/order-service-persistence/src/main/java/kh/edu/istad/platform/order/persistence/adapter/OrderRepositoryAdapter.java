package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.valueobject.*;
import kh.edu.istad.platform.order.persistence.entity.OrderEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderItemEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderItemEntityId;
import kh.edu.istad.platform.order.persistence.repository.OrderItemReactiveRepository;
import kh.edu.istad.platform.order.persistence.repository.OrderReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderReactiveRepository orderRepository;
    private final OrderItemReactiveRepository orderItemRepository;

    @Override
    @Transactional
    public Mono<Order> save(Order order) {
        OrderEntity entity = toEntity(order);

        return orderRepository.save(entity)
                .flatMap(savedOrder ->
                        saveItems(savedOrder, order.getItems())
                                .collectList()
                                .map(savedItems ->
                                        // Pass the incoming order's deliveryAddress to avoid null validation failure
                                        toDomain(savedOrder, savedItems, order.getDeliveryAddress())
                                )
                );
    }

    private Flux<OrderItemEntity> saveItems(
            OrderEntity savedOrder,
            List<OrderItem> items
    ) {
        List<OrderItemEntity> entities = items.stream()
                .map(item -> {
                    OrderItemEntity entity = new OrderItemEntity();

                    entity.setId(new OrderItemEntityId(
                            item.getId().value(),
                            savedOrder.getId()
                    ));

                    entity.setProductId(item.getProduct().productId());
                    entity.setProductName(item.getProduct().name());
                    entity.setQuantity(item.getQuantity());
                    entity.setPrice(item.getPrice().amount());
                    entity.setSubTotal(item.getSubTotal().amount());

                    return entity;
                })
                .toList();

        return orderItemRepository.saveAll(entities);
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();

        entity.setId(order.getId().value());
        entity.setCustomerId(order.getCustomerId().value());
        entity.setBusinessId(order.getBusinessId().value());
        entity.setPrice(order.getPrice().amount());
        entity.setTrackingId(order.getTrackingId().value());
        entity.setOrderStatus(order.getOrderStatus() != null ? order.getOrderStatus().name() : null);

        // If your OrderEntity and orders table have address columns, persist them here:
        /*
        if (order.getDeliveryAddress() != null) {
            entity.setStreet(order.getDeliveryAddress().street());
            entity.setPostalCode(order.getDeliveryAddress().postalCode());
            entity.setCity(order.getDeliveryAddress().city());
        }
        */

        return entity;
    }

    private Order toDomain(
            OrderEntity entity,
            List<OrderItemEntity> itemEntities,
            StreetAddress deliveryAddress
    ) {
        List<OrderItem> items = itemEntities.stream()
                .map(item -> OrderItem.builder()
                        .id(new OrderItemId(item.getId().id()))
                        .orderId(new OrderId(item.getId().orderId()))
                        .product(new Product(
                                item.getProductId(),
                                item.getProductName()
                        ))
                        .quantity(item.getQuantity())
                        .price(new Money(item.getPrice()))
                        .build())
                .toList();

        return Order.builder()
                .id(new OrderId(entity.getId()))
                .customerId(new CustomerId(entity.getCustomerId()))
                .businessId(new BusinessId(entity.getBusinessId()))
                .deliveryAddress(deliveryAddress) // <--- SET THE ADDRESS HERE
                .price(new Money(entity.getPrice()))
                .items(items)
                .trackingId(new TrackingId(entity.getTrackingId()))
                .orderStatus(entity.getOrderStatus() != null
                        ? OrderStatus.valueOf(entity.getOrderStatus())
                        : null)
                .build();
    }
}