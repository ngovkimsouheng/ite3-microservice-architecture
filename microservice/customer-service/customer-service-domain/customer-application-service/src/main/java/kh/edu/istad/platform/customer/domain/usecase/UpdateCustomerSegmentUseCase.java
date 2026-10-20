package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.dto.CustomerSegmentResult;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerSegmentCommand;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateCustomerSegmentUseCase {

    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<CustomerSegmentResult> execute(UpdateCustomerSegmentCommand command) {
        log.info("Update customer segment: {}", command);

        return customerSegmentRepository.findById(new CustomerSegmentId(command.id()))
                .switchIfEmpty(Mono.error(new CustomerDomainException("Customer segment not found: " + command.id())))
                .flatMap(segment -> {
                    segment.updateSegment(command.name(), command.description(), command.segmentType());
                    return customerSegmentRepository.save(segment);
                })
                .map(saved -> new CustomerSegmentResult(
                        saved.getId().value(),
                        saved.getName(),
                        saved.getDescription(),
                        saved.getSegmentType(),
                        saved.getCreatedAt(),
                        saved.getModifiedAt()
                ));
    }
}