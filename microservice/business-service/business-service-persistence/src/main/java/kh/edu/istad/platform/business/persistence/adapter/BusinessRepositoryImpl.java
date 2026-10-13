package kh.edu.istad.platform.business.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.entity.Product;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
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
                    .available(p.isAvailable())
                    .build())
                .toList();

        BusinessDocument doc = BusinessDocument.builder()
            .id(business.getId().value())
            .name(business.getName())
            .active(business.isActive())
            .products(productDocs)
            .build();

        return mongoRepository.save(doc)
            .map(saved -> {
                List<Product> products = saved.getProducts() == null ? Collections.emptyList() :
                    saved.getProducts().stream()
                        .map(p -> new Product(p.getId(), p.getName(), new Money(p.getPrice()), p.isAvailable()))
                        .toList();

                return Business.builder()
                    .businessId(new BusinessId(saved.getId()))
                    .name(saved.getName())
                    .active(saved.isActive())
                    .products(products)
                    .build();
            });
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return mongoRepository.existsByName(name);
    }
}