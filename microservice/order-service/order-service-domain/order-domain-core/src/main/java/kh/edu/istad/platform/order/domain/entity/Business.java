package kh.edu.istad.platform.order.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.BusinessId;

import java.util.List;

public class Business extends AggregateRoot<BusinessId> {
    private final List<Product> products;
    private boolean active;

    private Business(Builder builder) {
        super.setId(builder.businessId);
        products = builder.products;
        active = builder.active;
    }

    public List<Product> getProducts() {
        return products;
    }

    public boolean isActive() {
        return active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BusinessId businessId;
        private List<Product> products;
        private boolean active;

        private Builder() {
        }

        public Builder businessId(BusinessId val) {
            businessId = val;
            return this;
        }

        public Builder products(List<Product> val) {
            products = val;
            return this;
        }

        public Builder active(boolean val) {
            active = val;
            return this;
        }

        public Business build() {
            return new Business(this);
        }
    }
}