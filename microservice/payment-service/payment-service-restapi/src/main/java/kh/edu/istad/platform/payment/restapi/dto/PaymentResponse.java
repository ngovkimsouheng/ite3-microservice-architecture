package kh.edu.istad.platform.payment.restapi.dto;

import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponse {
    private final UUID paymentId;
    private final UUID orderId;
    private final PaymentStatus paymentStatus;
    private final ZonedDateTime createdAt;
    private final List<String> failureMessages;
}
