package kh.edu.istad.platform.business.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessCommand;
import kh.edu.istad.platform.business.domain.port.in.CreateBusinessUseCase;
import kh.edu.istad.platform.business.restapi.dto.CreateBusinessRequest;
import kh.edu.istad.platform.business.restapi.dto.CreateBusinessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final CreateBusinessUseCase createBusinessUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreateBusinessResponse> createBusiness(@Valid @RequestBody CreateBusinessRequest request) {
        CreateBusinessCommand command = new CreateBusinessCommand(
            request.name(),
            request.active(),
            request.products().stream()
                .map(p -> new CreateBusinessCommand.ProductItem(
                    p.id(),
                    p.name(),
                    p.price(),
                    p.available()
                ))
                .toList()
        );

        return createBusinessUseCase.execute(command)
            .map(result -> new CreateBusinessResponse(
                result.businessId(),
                result.name(),
                result.active(),
                result.products().stream()
                    .map(p -> new CreateBusinessResponse.ProductResponse(
                        p.id(),
                        p.name(),
                        p.price(),
                        p.available()
                    ))
                    .toList(),
                result.message()
            ));
    }
}
