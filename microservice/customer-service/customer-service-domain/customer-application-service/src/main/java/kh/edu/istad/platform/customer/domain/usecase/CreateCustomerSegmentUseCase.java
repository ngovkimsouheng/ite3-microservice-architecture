package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.dto.CreateCustomerSegmentCommand;
import kh.edu.istad.platform.customer.domain.dto.CustomerSegmentResult;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateCustomerSegmentUseCase {

    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<CustomerSegmentResult> execute(CreateCustomerSegmentCommand command) {
        log.info("Create customer segment: {}", command);

        CustomerSegment segment = CustomerSegment.builder()
                .name(command.name())
                .description(command.description())
                .segmentType(command.segmentType())
                .build();

        segment.initiateSegment();

        return customerSegmentRepository.save(segment)
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