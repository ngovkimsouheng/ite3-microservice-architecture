package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Business extends AggregateRoot<BusinessId> {
    private final String name;
    private final List<Product> products;
    private final boolean active;

    private Business(Builder builder) {
        super.setId(builder.businessId != null ? builder.businessId : new BusinessId(UUID.randomUUID()));
        this.name = builder.name;
        this.products = builder.products != null ? new ArrayList<>(builder.products) : new ArrayList<>();
        this.active = builder.active != null ? builder.active : true;
        validate();
    }

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessDomainException("Business name cannot be null or blank");
        }
        if (products.isEmpty()) {
            throw new BusinessDomainException("Business must register at least one product");
        }
    }

    public String getName() {
        return name;
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public boolean isActive() {
        return active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BusinessId businessId;
        private String name;
        private List<Product> products;
        private Boolean active;

        public Builder businessId(BusinessId businessId) {
            this.businessId = businessId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder products(List<Product> products) {
            this.products = products;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Business build() {
            return new Business(this);
        }
    }
}