package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.valueobject.OrderId;
import kh.edu.istad.platform.order.domain.valueobject.OrderItemId;
import kh.edu.istad.platform.order.domain.valueobject.ProductId;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;
import kh.edu.istad.platform.order.persistence.entity.OrderEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderItemEntity;
import kh.edu.istad.platform.order.persistence.repository.OrderReactiveRepository;
import kh.edu.istad.platform.order.persistence.repository.OrderItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderReactiveRepository orderRepository;
    private final OrderItemReactiveRepository orderItemRepository;

    @Override
    public Mono<Order> save(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(order.getId().value());
        entity.setCustomerId(order.getCustomerId().value());
        entity.setBusinessId(order.getBusinessId().value());
        entity.setTrackingId(order.getTrackingId().value());
        entity.setPrice(order.getPrice().amount());
        entity.setOrderStatus(order.getOrderStatus());
        entity.setStreet(order.getDeliveryAddress().street());
        entity.setPostalCode(order.getDeliveryAddress().postalCode());
        entity.setCity(order.getDeliveryAddress().city());

        return orderRepository.save(entity)
                .flatMap(savedOrder ->
                        reactor.core.publisher.Flux.fromIterable(order.getItems())
                                .map(item -> {
                                    OrderItemEntity itemEntity =
                                            new OrderItemEntity();

                                    itemEntity.setOrderItemId(
                                            item.getId() == null
                                                    ? null
                                                    : item.getId().value()
                                    );
                                    itemEntity.setOrderId(
                                            savedOrder.getOrderId()
                                    );
                                    itemEntity.setProductId(
                                            item.getProduct().getId().value()
                                    );
                                    itemEntity.setQuantity(item.getQuantity());
                                    itemEntity.setPrice(item.getPrice().amount());
                                    itemEntity.setSubTotal(
                                            item.getSubTotal().amount()
                                    );

                                    return itemEntity;
                                })
                                .concatMap(orderItemRepository::save)
                                .then(Mono.just(order))
                );
    }

    @Override
    public Mono<Order> findById(OrderId orderId) {
        return orderRepository.findById(orderId.value())
                .flatMap(entity ->
                        orderItemRepository.findAllByOrderId(
                                        entity.getOrderId()
                                )
                                .map(itemEntity ->
                                        OrderItem.Builder.builder()
                                                .id(new OrderItemId(
                                                        itemEntity.getOrderItemId()
                                                ))
                                                .product(new Product(
                                                        new ProductId(
                                                                itemEntity.getProductId()
                                                        ),
                                                        "",
                                                        new Money(itemEntity.getPrice())
                                                ))
                                                .quantity(itemEntity.getQuantity())
                                                .price(new Money(itemEntity.getPrice()))
                                                .subTotal(new Money(itemEntity.getSubTotal()))
                                                .build()
                                )
                                .collectList()
                                .map(items -> Order.Builder.builder()
                                        .id(new OrderId(entity.getOrderId()))
                                        .customerId(new CustomerId(
                                                entity.getCustomerId()
                                        ))
                                        .businessId(new BusinessId(
                                                entity.getBusinessId()
                                        ))
                                        .deliveryAddress(new StreetAddress(
                                                UUID.randomUUID(),
                                                entity.getStreet(),
                                                entity.getPostalCode(),
                                                entity.getCity()
                                        ))
                                        .price(new Money(entity.getPrice()))
                                        .orderStatus(entity.getOrderStatus())
                                        .trackingId(new TrackingId(
                                                entity.getTrackingId()
                                        ))
                                        .items(items)
                                        .build()
                                )
                );
    }
}
