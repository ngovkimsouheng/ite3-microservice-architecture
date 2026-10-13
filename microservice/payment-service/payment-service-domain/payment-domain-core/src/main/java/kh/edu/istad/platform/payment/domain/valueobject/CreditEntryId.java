package kh.edu.istad.platform.payment.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class CreditEntryId {
    private final UUID value;

    public CreditEntryId(UUID value) {
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditEntryId that = (CreditEntryId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }
}
