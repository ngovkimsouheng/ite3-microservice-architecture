package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.usecase.CreateSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.DeleteSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.QuerySegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.UpdateSegmentUseCase;
import kh.edu.istad.platform.customer.restapi.dto.CustomerSegmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/segments")
public class CustomerSegmentController {

    private final CreateSegmentUseCase createSegmentUseCase;
    private final QuerySegmentUseCase querySegmentUseCase;
    private final UpdateSegmentUseCase updateSegmentUseCase;
    private final DeleteSegmentUseCase deleteSegmentUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomerSegment> create(@Valid @RequestBody CustomerSegmentRequest request) {
        CustomerSegment domain = CustomerSegment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .segmentType(request.getSegmentType())
                .build();
        return createSegmentUseCase.execute(domain);
    }

    @GetMapping
    public Flux<CustomerSegment> findAll() {
        return querySegmentUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Mono<CustomerSegment> findById(@PathVariable String id) {
        return querySegmentUseCase.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<CustomerSegment> update(@PathVariable String id, @Valid @RequestBody CustomerSegmentRequest request) {
        CustomerSegment domain = CustomerSegment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .segmentType(request.getSegmentType())
                .build();
        return updateSegmentUseCase.execute(id, domain);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return deleteSegmentUseCase.execute(id);
    }
}