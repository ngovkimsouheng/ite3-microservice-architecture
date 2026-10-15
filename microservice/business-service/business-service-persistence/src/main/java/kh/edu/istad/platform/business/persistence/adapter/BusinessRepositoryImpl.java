package kh.edu.istad.platform.business.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.entity.Product;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.domain.valueobject.BusinessName;
import kh.edu.istad.platform.business.persistence.document.BusinessDocument;
import kh.edu.istad.platform.business.persistence.repository.BusinessReactiveMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BusinessRepositoryImpl implements BusinessRepository {

    private final BusinessReactiveMongoRepository mongoRepository;

    @Override
    public Mono<Business> save(Business business) {
        List<BusinessDocument.ProductDocument> productDocs = business.getProducts() == null ? Collections.emptyList() :
                business.getProducts().stream()
                        .map(p -> BusinessDocument.ProductDocument.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .price(p.getPrice().amount())
                                .quantity(p.getQuantity())
                                .available(p.isAvailable())
                                .build())
                        .toList();

        BusinessDocument doc = BusinessDocument.builder()
                .id(business.getId().value())
                .legalName(business.getBusinessName() != null ? business.getBusinessName().legalName() : null)
                .displayName(business.getBusinessName() != null ? business.getBusinessName().displayName() : null)
                .email(business.getBusinessContact() != null ? business.getBusinessContact().email() : null)
                .phoneNumber(business.getBusinessContact() != null ? business.getBusinessContact().phoneNumber() : null)
                .active(business.isActive())
                .products(productDocs)
                .build();

        return mongoRepository.save(doc).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return mongoRepository.existsByLegalName(name);
    }

    @Override
    public Mono<Business> findById(BusinessId businessId) {
        return mongoRepository.findById(businessId.value()).map(this::toDomain);
    }

    private Business toDomain(BusinessDocument saved) {
        List<Product> products = saved.getProducts() == null ? Collections.emptyList() :
                saved.getProducts().stream()
                        .map(p -> new Product(p.getId(), p.getName(), new Money(p.getPrice()), p.getQuantity(), p.isAvailable()))
                        .toList();

        return Business.builder()
                .businessId(new BusinessId(saved.getId()))
                .businessName(new BusinessName(saved.getLegalName(), saved.getDisplayName()))
                .businessContact(new BusinessContact(saved.getEmail(), saved.getPhoneNumber()))
                .active(saved.isActive())
                .products(products)
                .build();
    }
}
