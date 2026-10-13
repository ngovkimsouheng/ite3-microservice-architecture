package kh.edu.istad.platform.payment.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentResult;
import kh.edu.istad.platform.payment.domain.entity.CreditEntry;
import kh.edu.istad.platform.payment.domain.entity.CreditHistory;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentEvent;
import kh.edu.istad.platform.payment.domain.exception.PaymentDomainException;
import kh.edu.istad.platform.payment.domain.port.out.CreditEntryRepository;
import kh.edu.istad.platform.payment.domain.port.out.CreditHistoryRepository;
import kh.edu.istad.platform.payment.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainService;
import kh.edu.istad.platform.payment.domain.valueobject.OrderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessPaymentUseCase {

    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;

    public ProcessPaymentResult execute(ProcessPaymentCommand command) {
        log.info("ProcessPaymentUseCase.execute: command = {}", command);

        CustomerId customerId = new CustomerId(command.customerId());
        Payment payment = Payment.Builder.builder()
                .orderId(new OrderId(command.orderId()))
                .customerId(customerId)
                .price(new Money(command.price()))
                .build();

        CreditEntry creditEntry = creditEntryRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new PaymentDomainException("Could not find credit entry for customer: " + customerId.value()));

        List<CreditHistory> creditHistories = new ArrayList<>(creditHistoryRepository.findByCustomerId(customerId));
        List<String> failureMessages = new ArrayList<>();

        PaymentEvent event = paymentDomainService.validateAndInitiatePayment(
                payment, creditEntry, creditHistories, failureMessages
        );

        Payment savedPayment = paymentRepository.save(payment);

        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            if (!creditHistories.isEmpty()) {
                creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
            }
        }

        return new ProcessPaymentResult(
                savedPayment.getId().getValue(),
                savedPayment.getOrderId().getValue(),
                savedPayment.getPaymentStatus(),
                savedPayment.getCreatedAt(),
                failureMessages
        );
    }
}
