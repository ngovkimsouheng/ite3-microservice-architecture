package kh.edu.istad.platform.customer.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;

import java.time.Instant;
import java.util.UUID;

public class CustomerSegment extends BaseEntity<CustomerSegmentId> {
    private String name;
    private String description;
    private CustomerSegmentType segmentType;
    private Instant createdAt;
    private Instant modifiedAt;

    public void initiateSegment() {
        if (super.getId() != null) {
            throw new CustomerDomainException("Customer segment id must be null before initiation");
        }
        super.setId(new CustomerSegmentId(UUID.randomUUID().toString()));
        this.createdAt = Instant.now();
        this.modifiedAt = Instant.now();
    }

    public void updateSegment(String name, String description, CustomerSegmentType segmentType) {
        if (name == null || name.isBlank()) {
            throw new CustomerDomainException("Segment name must not be blank");
        }
        if (segmentType == null) {
            throw new CustomerDomainException("Segment type must not be null");
        }
        this.name = name;
        this.description = description;
        this.segmentType = segmentType;
        this.modifiedAt = Instant.now();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CustomerSegmentType getSegmentType() {
        return segmentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    private CustomerSegment(Builder builder) {
        super.setId(builder.id);
        this.name = builder.name;
        this.description = builder.description;
        this.segmentType = builder.segmentType;
        this.createdAt = builder.createdAt;
        this.modifiedAt = builder.modifiedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CustomerSegmentId id;
        private String name;
        private String description;
        private CustomerSegmentType segmentType;
        private Instant createdAt;
        private Instant modifiedAt;

        private Builder() {}

        public Builder id(CustomerSegmentId val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder description(String val) {
            description = val;
            return this;
        }

        public Builder segmentType(CustomerSegmentType val) {
            segmentType = val;
            return this;
        }

        public Builder createdAt(Instant val) {
            createdAt = val;
            return this;
        }

        public Builder modifiedAt(Instant val) {
            modifiedAt = val;
            return this;
        }

        public CustomerSegment build() {
            return new CustomerSegment(this);
        }
    }
}