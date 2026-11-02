
package kh.edu.istad.platform.order.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.order.domain.event.OrderCancelledEvent;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.domain.event.OrderPaidEvent;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order extends AggregateRoot<OrderId> {

    private CustomerId customerId;
    private BusinessId businessId;
    private StreetAddress deliveryAddress;
    private Money price;
    private List<OrderItem> items;
    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private ZonedDateTime createdAt;
    private List<String> failureMessages;

    private final List<DomainEvent<Order>> domainEvents = new ArrayList<>();

    private Order(Builder builder) {
        setId(builder.id);

        customerId = builder.customerId;
        businessId = builder.businessId;
        deliveryAddress = builder.deliveryAddress;
        price = builder.price;
        items = builder.items != null
                ? new ArrayList<>(builder.items)
                : new ArrayList<>();
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        createdAt = builder.createdAt;
        failureMessages = builder.failureMessages != null
                ? new ArrayList<>(builder.failureMessages)
                : new ArrayList<>();

        validateOrder();
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<DomainEvent<Order>> getDomainEvents() {
        return List.copyOf(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public void validateOrder() {
        if (getId() == null) {
            throw new OrderDomainException("Order ID cannot be null");
        }
        if (customerId == null) {
            throw new OrderDomainException("Customer ID cannot be null");
        }
        if (businessId == null) {
            throw new OrderDomainException("Business ID cannot be null");
        }
        if (deliveryAddress == null) {
            throw new OrderDomainException("Delivery address cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new OrderDomainException(
                    "Order must contain at least one item"
            );
        }
        if (price == null) {
            throw new OrderDomainException("Order price cannot be null");
        }

        BigDecimal itemsTotal = items.stream()
                .map(OrderItem::getSubTotal)
                .map(Money::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (price.amount().compareTo(itemsTotal) != 0) {
            throw new OrderDomainException(
                    "Order price must equal the sum of item subtotals"
            );
        }
    }

    public OrderCreatedEvent initializeOrder() {
        if (orderStatus != null) {
            throw new OrderDomainException(
                    "Order has already been initialized"
            );
        }

        orderStatus = OrderStatus.PENDING;
        trackingId = TrackingId.generate();
        createdAt = ZonedDateTime.now();
        failureMessages.clear();

        OrderCreatedEvent event = new OrderCreatedEvent(this, createdAt);
        domainEvents.add(event);

        return event;
    }

    public void pay() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException(
                    "Only pending orders can be paid"
            );
        }

        orderStatus = OrderStatus.PAID;
        domainEvents.add(
                new OrderPaidEvent(this, ZonedDateTime.now())
        );
    }

    public void approve() {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException(
                    "Only paid orders can be approved"
            );
        }

        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel() {
        if (orderStatus != OrderStatus.PENDING
                && orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException(
                    "Only pending or paid orders can begin cancellation"
            );
        }

        orderStatus = OrderStatus.CANCELLING;
    }

    public void cancel() {
        if (orderStatus != OrderStatus.CANCELLING) {
            throw new OrderDomainException(
                    "Order must be cancelling before it can be cancelled"
            );
        }

        orderStatus = OrderStatus.CANCELLED;
        domainEvents.add(
                new OrderCancelledEvent(this, ZonedDateTime.now())
        );
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public BusinessId getBusinessId() {
        return businessId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getFailureMessages() {
        return List.copyOf(failureMessages);
    }

    public static final class Builder {

        private OrderId id;
        private CustomerId customerId;
        private BusinessId businessId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private ZonedDateTime createdAt;
        private List<String> failureMessages;

        private Builder() {
        }

        public Builder id(OrderId val) {
            id = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder businessId(BusinessId val) {
            businessId = val;
            return this;
        }

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder createdAt(ZonedDateTime val) {
            createdAt = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}