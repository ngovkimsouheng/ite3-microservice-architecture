//package kh.edu.istad.platform.order.persistence.adapter;
//
//import kh.edu.istad.platform.order.domain.entity.Business;
//import kh.edu.istad.platform.order.domain.port.out.BusinessRepository;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//public class BusinessRepositoryImpl implements BusinessRepository {
//
//    @Override
//    public Optional<Business> findBusinessInformation(Business business) {
//        return Optional.of(Business.builder()
//                .businessId(business.getId())
//                .products(business.getProducts())
//                .active(true)
//                .build());
//    }
//}

package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.order.domain.entity.Business;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.port.out.BusinessRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class BusinessRepositoryImpl implements BusinessRepository {

    @Override
    public Optional<Business> findBusinessInformation(Business business) {
        List<Product> businessProducts = business.getProducts().stream()
                .map(product -> new Product(
                        product.getId(),
                        "Test Product",
                        new Money(new BigDecimal("10.00"))
                ))
                .toList();

        return Optional.of(Business.builder()
                .businessId(business.getId())
                .products(businessProducts)
                .active(true)
                .build());
    }
}