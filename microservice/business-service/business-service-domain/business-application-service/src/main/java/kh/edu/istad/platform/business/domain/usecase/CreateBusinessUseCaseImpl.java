package kh.edu.istad.platform.business.domain.usecase;

import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessResult;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.entity.Product;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.port.in.CreateBusinessUseCase;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.domain.valueobject.BusinessName;
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
        log.info("Processing business creation: legalName = {}", command.legalName());

        return businessRepository.existsByName(command.legalName())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new BusinessDomainException("Business with name '" + command.legalName() + "' already exists."));
                    }

                    List<Product> products = command.products() == null ? Collections.emptyList() :
                            command.products().stream()
                                    .map(p -> new Product(
                                            p.id() != null ? p.id() : UUID.randomUUID(),
                                            p.name(),
                                            new Money(p.price()),
                                            p.quantity() != null ? p.quantity() : 0,
                                            p.available() != null ? p.available() : true
                                    ))
                                    .toList();

                    Business business = Business.builder()
                            .businessName(new BusinessName(command.legalName(), command.displayName()))
                            .businessContact(new BusinessContact(command.email(), command.phoneNumber()))
                            .products(products)
                            .active(command.active() != null ? command.active() : true)
                            .build();

                    business.initializeBusiness();
                    business.validateBusiness();

                    return businessRepository.save(business);
                })
                .map(saved -> new CreateBusinessResult(
                        saved.getId().value(),
                        saved.getBusinessName().legalName(),
                        saved.getBusinessName().displayName(),
                        saved.isActive(),
                        saved.getProducts().stream()
                                .map(p -> new CreateBusinessResult.ProductItemResult(
                                        p.getId(),
                                        p.getName(),
                                        p.getPrice().amount(),
                                        p.getQuantity(),
                                        p.isAvailable()
                                ))
                                .toList(),
                        "Business created successfully"
                ));
    }
}
