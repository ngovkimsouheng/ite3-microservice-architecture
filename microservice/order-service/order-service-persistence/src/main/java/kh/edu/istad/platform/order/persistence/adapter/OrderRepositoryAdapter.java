package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.valueobject.*;
import kh.edu.istad.platform.order.persistence.entity.OrderEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderItemEntity;
import kh.edu.istad.platform.order.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(order.getId().value());
        orderEntity.setCustomerId(order.getCustomerId().value());
        orderEntity.setBusinessId(order.getBusinessId().value());
        orderEntity.setTrackingId(order.getTrackingId().value());
        orderEntity.setPrice(order.getPrice().amount());
        orderEntity.setOrderStatus(order.getOrderStatus());
        orderEntity.setStreet(order.getDeliveryAddress().street());
        orderEntity.setPostalCode(order.getDeliveryAddress().postalCode());
        orderEntity.setCity(order.getDeliveryAddress().city());

        List<OrderItemEntity> itemEntities = order.getItems().stream().map(item -> {
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrderItemId(item.getId().value());
            itemEntity.setOrder(orderEntity);
            itemEntity.setProductId(item.getProduct().getId().value());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setPrice(item.getPrice().amount());
            itemEntity.setSubTotal(item.getSubTotal().amount());
            return itemEntity;
        }).toList();

        orderEntity.setItems(itemEntities);
        orderJpaRepository.save(orderEntity);

        return order;
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return orderJpaRepository.findById(orderId.value()).map(entity -> {
            List<OrderItem> items = entity.getItems().stream().map(itemEntity ->
                    OrderItem.Builder.builder()
                            .id(new OrderItemId(itemEntity.getOrderItemId()))
                            .product(new Product(new ProductId(itemEntity.getProductId()), "", new Money(itemEntity.getPrice())))
                            .quantity(itemEntity.getQuantity())
                            .price(new Money(itemEntity.getPrice()))
                            .subTotal(new Money(itemEntity.getSubTotal()))
                            .build()
            ).toList();

            return Order.Builder.builder()
                    .id(new OrderId(entity.getOrderId()))
                    .customerId(new CustomerId(entity.getCustomerId()))
                    .businessId(new BusinessId(entity.getBusinessId()))
                    .deliveryAddress(new StreetAddress(UUID.randomUUID(), entity.getStreet(), entity.getPostalCode(), entity.getCity()))
                    .price(new Money(entity.getPrice()))
                    .orderStatus(entity.getOrderStatus())
                    .trackingId(new TrackingId(entity.getTrackingId()))
                    .items(items)
                    .build();
        });
    }
}