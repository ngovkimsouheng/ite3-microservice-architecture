package kh.edu.istad.platform.payment.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.payment.domain.entity.CreditHistory;
import kh.edu.istad.platform.payment.domain.port.out.CreditHistoryRepository;
import kh.edu.istad.platform.payment.domain.valueobject.CreditHistoryId;
import kh.edu.istad.platform.payment.persistence.entity.CreditHistoryEntity;
import kh.edu.istad.platform.payment.persistence.repository.CreditHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreditHistoryRepositoryAdapter implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository repository;

    @Override
    public CreditHistory save(CreditHistory history) {
        repository.save(CreditHistoryEntity.builder()
                .id(history.getId().getValue())
                .customerId(history.getCustomerId().value())
                .amount(history.getAmount().amount())
                .type(history.getTransactionType())
                .build());
        return history;
    }

    @Override
    public List<CreditHistory> findByCustomerId(CustomerId customerId) {
        return repository.findByCustomerId(customerId.value()).stream()
                .map(h -> new CreditHistory(
                        new CreditHistoryId(h.getId()),
                        new CustomerId(h.getCustomerId()),
                        new Money(h.getAmount()),
                        h.getType()
                ))
                .toList();
    }
}
