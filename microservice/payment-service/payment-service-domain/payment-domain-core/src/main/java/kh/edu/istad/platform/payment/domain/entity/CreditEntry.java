package kh.edu.istad.platform.payment.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.payment.domain.valueobject.CreditEntryId;

import java.math.BigDecimal;

public class CreditEntry extends BaseEntity<CreditEntryId> {
    private final CustomerId customerId;
    private Money totalCreditAmount;

    public CreditEntry(CreditEntryId creditEntryId, CustomerId customerId, Money totalCreditAmount) {
        super.setId(creditEntryId);
        this.customerId = customerId;
        this.totalCreditAmount = totalCreditAmount;
    }

    public void addCreditAmount(Money amount) {
        BigDecimal newAmount = this.totalCreditAmount.amount().add(amount.amount());
        this.totalCreditAmount = new Money(newAmount);
    }

    public void subtractCreditAmount(Money amount) {
        BigDecimal newAmount = this.totalCreditAmount.amount().subtract(amount.amount());
        this.totalCreditAmount = new Money(newAmount);
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getTotalCreditAmount() {
        return totalCreditAmount;
    }
}
