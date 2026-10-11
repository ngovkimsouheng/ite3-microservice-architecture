package kh.edu.istad.common.domain.valueobject;

import java.math.BigDecimal;

public record Money(
        BigDecimal amount
) {

    public boolean isGreaterThanZero() {

        if (!(amount.compareTo(BigDecimal.ZERO) > 0)) {
            System.out.println("Money is not greater than zero");
            throw new RuntimeException("Money is not greater than zero");
        }
        return false;
    }
}
