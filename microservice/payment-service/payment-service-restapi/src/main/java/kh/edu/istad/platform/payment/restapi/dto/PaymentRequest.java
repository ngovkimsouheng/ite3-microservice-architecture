package kh.edu.istad.platform.payment.restapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull
    private UUID orderId;
    @NotNull
    private UUID customerId;
    @NotNull
    @Positive
    private BigDecimal price;
}
