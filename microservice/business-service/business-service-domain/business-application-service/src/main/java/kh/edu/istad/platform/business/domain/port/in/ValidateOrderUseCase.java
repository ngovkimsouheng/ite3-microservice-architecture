package kh.edu.istad.platform.business.domain.port.in;

import kh.edu.istad.platform.business.domain.dto.ValidateOrderCommand;
import kh.edu.istad.platform.business.domain.dto.ValidateOrderResult;
import reactor.core.publisher.Mono;

public interface ValidateOrderUseCase {
    Mono<ValidateOrderResult> execute(ValidateOrderCommand command);
}
