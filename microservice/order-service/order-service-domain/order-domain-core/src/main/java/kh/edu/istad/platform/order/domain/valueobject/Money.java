package kh.edu.istad.platform.order.domain.valueobject;

import java.math.BigDecimal;

public record Money(BigDecimal amount) {
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException(
                    "Money amount cannot be null"
            );
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Money amount cannot be negative"
            );
        }
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money multiply(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be negative"
            );
        }

        return new Money(
                amount.multiply(BigDecimal.valueOf(quantity))
        );
    }
}