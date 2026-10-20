package kh.edu.istad.platform.customer.domain.valueobject;

import java.util.Objects;

public class CustomerSegmentId {
    private final String value;

    public CustomerSegmentId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerSegmentId that = (CustomerSegmentId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}