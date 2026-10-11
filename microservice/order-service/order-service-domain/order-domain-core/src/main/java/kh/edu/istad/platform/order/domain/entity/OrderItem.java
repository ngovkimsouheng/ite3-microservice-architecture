package kh.edu.istad.platform.order.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.order.domain.valueobject.OrderId;
import kh.edu.istad.platform.order.domain.valueobject.OrderItemId;

import java.math.BigDecimal;

public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private final Product product;
    private final Integer quantity;
    private final Money price;
    private final Money subTotal;

    void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        super.setId(orderItemId);
    }

    boolean isPriceValid() {
        if (price == null || price.amount() == null || product.getPrice() == null || product.getPrice().amount() == null) {
            return false;
        }
        BigDecimal expectedSubTotal = price.amount().multiply(BigDecimal.valueOf(quantity));
        return price.amount().compareTo(BigDecimal.ZERO) > 0
                && price.amount().compareTo(product.getPrice().amount()) == 0
                && subTotal != null
                && subTotal.amount() != null
                && subTotal.amount().compareTo(expectedSubTotal) == 0;
    }

    private OrderItem(Builder builder) {
        super.setId(builder.id);
        this.product = builder.product;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.subTotal = builder.subTotal;
    }

    public static final class Builder {
        private OrderItemId id;
        private Product product;
        private Integer quantity;
        private Money price;
        private Money subTotal;

        private Builder() {}

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(OrderItemId val) {
            id = val;
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

        public Builder subTotal(Money val) {
            subTotal = val;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }

    public OrderId getOrderId() { return orderId; }
    public Product getProduct() { return product; }
    public Integer getQuantity() { return quantity; }
    public Money getPrice() { return price; }
    public Money getSubTotal() { return subTotal; }
}