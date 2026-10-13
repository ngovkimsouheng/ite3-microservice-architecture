package kh.edu.istad.platform.payment.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.payment.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentId;
import kh.edu.istad.platform.payment.persistence.entity.PaymentEntity;
import kh.edu.istad.platform.payment.persistence.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        paymentJpaRepository.save(PaymentEntity.builder()
                .id(payment.getId().getValue())
                .customerId(payment.getCustomerId().value())
                .orderId(payment.getOrderId().getValue())
                .price(payment.getPrice().amount())
                .status(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build());
        return payment;
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return paymentJpaRepository.findById(paymentId)
                .map(entity -> Payment.Builder.builder()
                        .id(new PaymentId(entity.getId()))
                        .customerId(new CustomerId(entity.getCustomerId()))
                        .orderId(new OrderId(entity.getOrderId()))
                        .price(new Money(entity.getPrice()))
                        .paymentStatus(entity.getStatus())
                        .createdAt(entity.getCreatedAt())
                        .build());
    }
}
