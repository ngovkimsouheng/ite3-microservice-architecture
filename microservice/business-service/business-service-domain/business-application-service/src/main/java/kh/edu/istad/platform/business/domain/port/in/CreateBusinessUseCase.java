package kh.edu.istad.platform.business.domain.port.in;

import kh.edu.istad.platform.business.domain.dto.CreateBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessResult;
import reactor.core.publisher.Mono;

public interface CreateBusinessUseCase {
    Mono<CreateBusinessResult> execute(CreateBusinessCommand command);
}
