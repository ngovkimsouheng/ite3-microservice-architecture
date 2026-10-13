package kh.edu.istad.platform.payment.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.payment.domain.entity.CreditEntry;
import kh.edu.istad.platform.payment.domain.port.out.CreditEntryRepository;
import kh.edu.istad.platform.payment.domain.valueobject.CreditEntryId;
import kh.edu.istad.platform.payment.persistence.entity.CreditEntryEntity;
import kh.edu.istad.platform.payment.persistence.repository.CreditEntryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreditEntryRepositoryAdapter implements CreditEntryRepository {

    private final CreditEntryJpaRepository repository;

    @Override
    public CreditEntry save(CreditEntry entry) {
        repository.save(CreditEntryEntity.builder()
                .id(entry.getId().getValue())
                .customerId(entry.getCustomerId().value())
                .totalCreditAmount(entry.getTotalCreditAmount().amount())
                .build());
        return entry;
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return repository.findByCustomerId(customerId.value())
                .map(e -> new CreditEntry(
                        new CreditEntryId(e.getId()),
                        new CustomerId(e.getCustomerId()),
                        new Money(e.getTotalCreditAmount())
                ));
    }
}
