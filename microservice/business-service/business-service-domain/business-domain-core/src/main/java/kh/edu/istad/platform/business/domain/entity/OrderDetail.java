package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.business.domain.valueobject.OrderStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OrderDetail {
    private final UUID orderId;
    private OrderStatus orderStatus;
    private final Money totalAmount;
    private final List<Product> products;

    public OrderDetail(UUID orderId, OrderStatus orderStatus, Money totalAmount, List<Product> products) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.products = products != null ? products : Collections.emptyList();
    }

    public UUID getOrderId() { return orderId; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public Money getTotalAmount() { return totalAmount; }
    public List<Product> getProducts() { return products; }
}
