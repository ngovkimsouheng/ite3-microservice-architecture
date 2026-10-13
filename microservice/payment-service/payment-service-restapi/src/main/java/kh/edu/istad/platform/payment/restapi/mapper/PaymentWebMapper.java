package kh.edu.istad.platform.payment.restapi.mapper;

import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentResult;
import kh.edu.istad.platform.payment.restapi.dto.PaymentProcessRequest;
import kh.edu.istad.platform.payment.restapi.dto.PaymentProcessResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentWebMapper {
    ProcessPaymentCommand toCommand(PaymentProcessRequest request);
    PaymentProcessResponse toResponse(ProcessPaymentResult result);
}
