package kh.edu.istad.platform.customer.restapi.controller;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/segments")
public class CustomerSegmentController {

    private final CustomerSegmentRepository customerSegmentRepository;

    @GetMapping
    public Flux<CustomerSegment> findAll() {
        return customerSegmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<CustomerSegment> findById(@PathVariable String id) {
        return customerSegmentRepository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomerSegment> create(@RequestBody CustomerSegment segment) {
        return customerSegmentRepository.save(segment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return customerSegmentRepository.deleteById(id);
    }
}