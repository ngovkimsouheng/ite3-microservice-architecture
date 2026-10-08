package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class InitiateCustomerUseCase {

    public InitiateCustomerResult execute(InitiateCustomerCommand command) {
        log.info("InitiateCustomerUseCase.execute: command = {}", command);
        return new InitiateCustomerResult(UUID.randomUUID());
    }
}
