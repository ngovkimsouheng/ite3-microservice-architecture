package kh.edu.istad.platform.business.domain.usecase;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessResult;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.entity.Product;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.port.in.CreateBusinessUseCase;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateBusinessUseCaseImpl implements CreateBusinessUseCase {

    private final BusinessRepository businessRepository;

    @Override
    public Mono<CreateBusinessResult> execute(CreateBusinessCommand command) {
        log.info("Processing business creation: name = {}", command.name());

        return businessRepository.existsByName(command.name())
            .flatMap(exists -> {
                if (Boolean.TRUE.equals(exists)) {
                    return Mono.error(new BusinessDomainException("Business with name '" + command.name() + "' already exists."));
                }

                List<Product> products = command.products() == null ? Collections.emptyList() :
                    command.products().stream()
                        .map(p -> new Product(
                            p.id() != null ? p.id() : UUID.randomUUID(),
                            p.name(),
                            new Money(p.price()),
                            p.available() != null ? p.available() : true
                        ))
                        .toList();

                Business business = Business.builder()
                    .businessId(new BusinessId(UUID.randomUUID()))
                    .name(command.name())
                    .products(products)
                    .active(command.active() != null ? command.active() : true)
                    .build();

                return businessRepository.save(business);
            })
            .map(saved -> new CreateBusinessResult(
                saved.getId().value(),
                saved.getName(),
                saved.isActive(),
                saved.getProducts().stream()
                    .map(p -> new CreateBusinessResult.ProductItemResult(
                        p.getId(),
                        p.getName(),
                        p.getPrice().amount(),
                        p.isAvailable()
                    ))
                    .toList(),
                "Business created successfully"
            ));
    }
}