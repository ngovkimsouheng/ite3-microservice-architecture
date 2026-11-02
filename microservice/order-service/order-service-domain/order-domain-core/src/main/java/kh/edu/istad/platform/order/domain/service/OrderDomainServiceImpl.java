package kh.edu.istad.platform.order.domain.service;

import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.event.OrderCancelledEvent;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.domain.event.OrderPaidEvent;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;

import java.time.ZonedDateTime;

public class OrderDomainServiceImpl implements OrderDomainService {
    @Override
    public OrderCreatedEvent createOrder(Order order) {

        if (order == null) {
            throw new OrderDomainException("Order is required");
        }

        return order.initializeOrder();
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {

        if (order == null) {
            throw new OrderDomainException("Order is required");
        }

        order.pay();

        return new OrderPaidEvent(
                order,
                ZonedDateTime.now()
        );
    }

    @Override
    public OrderCancelledEvent cancelOrder(Order order) {

        if (order == null) {
            throw new OrderDomainException("Order is required");
        }
        order.initCancel();
        order.cancel();

        return new OrderCancelledEvent(
                order,
                ZonedDateTime.now()
        );
    }
}