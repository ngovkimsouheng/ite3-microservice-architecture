package kh.edu.istad.platform.business.domain.usecase;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.business.domain.dto.ValidateOrderCommand;
import kh.edu.istad.platform.business.domain.dto.ValidateOrderResult;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.entity.OrderDetail;
import kh.edu.istad.platform.business.domain.entity.Product;
import kh.edu.istad.platform.business.domain.event.OrderApprovalEvent;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.port.in.ValidateOrderUseCase;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.business.domain.service.BusinessDomainService;
import kh.edu.istad.platform.business.domain.service.BusinessDomainServiceImpl;
import kh.edu.istad.platform.business.domain.valueobject.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidateOrderUseCaseImpl implements ValidateOrderUseCase {

    private final BusinessRepository businessRepository;
    private final BusinessDomainService businessDomainService = new BusinessDomainServiceImpl();

    @Override
    public Mono<ValidateOrderResult> execute(ValidateOrderCommand command) {
        log.info("Validating order approval: businessId = {}, orderId = {}", command.businessId(), command.orderId());

        return businessRepository.findById(new BusinessId(command.businessId()))
                .switchIfEmpty(Mono.error(new BusinessDomainException("Business not found with ID: " + command.businessId())))
                .map(business -> {
                    List<Product> orderProducts = command.products().stream()
                            .map(p -> {
                                String matchedName = business.getProducts().stream()
                                        .filter(bp -> bp.getId().equals(p.id()))
                                        .map(Product::getName)
                                        .findFirst()
                                        .orElse("Order Product");

                                return new Product(
                                        p.id(),
                                        matchedName,
                                        new Money(BigDecimal.ONE),
                                        p.quantity(),
                                        true
                                );
                            })
                            .toList();

                    OrderDetail orderDetail = new OrderDetail(
                            command.orderId(),
                            OrderStatus.PAID,
                            new Money(command.totalAmount()),
                            orderProducts
                    );

                    Business businessWithOrder = Business.builder()
                            .businessId(business.getId())
                            .businessName(business.getBusinessName())
                            .businessContact(business.getBusinessContact())
                            .active(business.isActive())
                            .products(business.getProducts())
                            .orderDetail(orderDetail)
                            .build();

                    List<String> failureMessages = new ArrayList<>();
                    OrderApprovalEvent event = businessDomainService.validateOrder(businessWithOrder, failureMessages);

                    return new ValidateOrderResult(
                            event.getOrderApproval().getId().value(),
                            event.getOrderApproval().getOrderId(),
                            event.getOrderApproval().getApprovalStatus(),
                            failureMessages
                    );
                });
    }
}
