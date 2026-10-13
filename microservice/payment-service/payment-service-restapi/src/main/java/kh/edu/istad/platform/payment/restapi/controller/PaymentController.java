package kh.edu.istad.platform.payment.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentResult;
import kh.edu.istad.platform.payment.domain.usecase.ProcessPaymentUseCase;
import kh.edu.istad.platform.payment.restapi.dto.PaymentProcessRequest;
import kh.edu.istad.platform.payment.restapi.dto.PaymentProcessResponse;
import kh.edu.istad.platform.payment.restapi.mapper.PaymentWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final PaymentWebMapper paymentWebMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PaymentProcessResponse processPayment(@Valid @RequestBody PaymentProcessRequest request) {
        ProcessPaymentResult result = processPaymentUseCase.execute(paymentWebMapper.toCommand(request));
        return paymentWebMapper.toResponse(result);
    }
}
