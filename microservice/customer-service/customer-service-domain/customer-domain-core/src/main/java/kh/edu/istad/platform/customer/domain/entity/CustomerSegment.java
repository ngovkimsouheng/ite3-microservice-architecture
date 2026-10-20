package kh.edu.istad.platform.customer.domain.entity;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import java.time.Instant;

public class CustomerSegment {
    private String id;
    private String name;
    private String description;
    private CustomerSegmentType segmentType;
    private Instant createdAt;
    private Instant modifiedAt;

    public CustomerSegment() {
    }

    public CustomerSegment(String id, String name, String description, CustomerSegmentType segmentType, Instant createdAt, Instant modifiedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.segmentType = segmentType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CustomerSegmentType getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(CustomerSegmentType segmentType) {
        this.segmentType = segmentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private CustomerSegmentType segmentType;
        private Instant createdAt;
        private Instant modifiedAt;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder segmentType(CustomerSegmentType segmentType) {
            this.segmentType = segmentType;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder modifiedAt(Instant modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public CustomerSegment build() {
            return new CustomerSegment(id, name, description, segmentType, createdAt, modifiedAt);
        }
    }
}