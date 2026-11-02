
package kh.edu.istad.platform.order.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.valueobject.Money;
import kh.edu.istad.platform.order.domain.valueobject.OrderItemId;

public class OrderItem extends BaseEntity<OrderItemId> {

    private OrderId orderId;
    private Product product;
    private Integer quantity;
    private Money price;
    private Money subTotal;

    private OrderItem(Builder builder) {
        if (builder.id == null) {
            throw new OrderDomainException(
                    "Order item ID cannot be null"
            );
        }

        if (builder.orderId == null) {
            throw new OrderDomainException(
                    "Order ID cannot be null"
            );
        }
        if (builder.product == null) {
            throw new OrderDomainException(
                    "Product cannot be null"
            );
        }
        if (builder.quantity == null || builder.quantity <= 0) {
            throw new OrderDomainException(
                    "Quantity must be greater than zero"
            );
        }
        if (builder.price == null) {
            throw new OrderDomainException(
                    "Price cannot be null"
            );
        }

        setId(builder.id);

        orderId = builder.orderId;
        product = builder.product;
        quantity = builder.quantity;
        price = builder.price;
        subTotal = price.multiply(quantity);
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    public Money getSubTotal() {
        return subTotal;
    }

    public static final class Builder {

        private OrderItemId id;
        private OrderId orderId;
        private Product product;
        private Integer quantity;
        private Money price;

        private Builder() {
        }

        public Builder id(OrderItemId val) {
            id = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder product(Product val) {
            product = val;
            return this;
        }

        public Builder quantity(Integer val) {
            quantity = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}