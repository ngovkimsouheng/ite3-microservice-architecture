package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;

import java.math.BigDecimal;
import java.util.UUID;

public class Product extends BaseEntity<UUID> {
    private final String name;
    private final Money price;
    private final int quantity;
    private final boolean available;

    public Product(UUID id, String name, Money price, int quantity, Boolean available) {
        super.setId(id != null ? id : UUID.randomUUID());
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.available = available != null ? available : true;
        validate();
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new BusinessDomainException("Product name cannot be empty");
        }
        if (price == null || price.amount() == null || price.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessDomainException("Product price must be greater than zero");
        }
        if (quantity < 0) {
            throw new BusinessDomainException("Product quantity cannot be negative");
        }
    }

    public String getName() { return name; }
    public Money getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public boolean isAvailable() { return available; }
}
