package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.domain.valueobject.BusinessName;
import kh.edu.istad.platform.business.domain.valueobject.OrderApprovalId;
import kh.edu.istad.platform.business.domain.valueobject.OrderApprovalStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Business extends AggregateRoot<BusinessId> {
    private BusinessName businessName;
    private BusinessContact businessContact;
    private OrderApproval orderApproval;
    private boolean active;
    private OrderDetail orderDetail;
    private final List<Product> products;

    public void validateBusiness() {
        if (businessName == null || businessName.legalName() == null || businessName.legalName().isBlank()) {
            throw new BusinessDomainException("Business legal name cannot be null or blank");
        }
        if (products.isEmpty()) {
            throw new BusinessDomainException("Business must register at least one product");
        }
    }

    public void initializeBusiness() {
        if (super.getId() == null) {
            super.setId(new BusinessId(UUID.randomUUID()));
        }
        this.active = true;
    }

    public void validateOrder(List<String> failureMessages) {
        if (orderDetail == null) {
            failureMessages.add("Order detail cannot be null!");
            return;
        }
        if (!active) {
            failureMessages.add("Business is currently not active!");
        }
        orderDetail.getProducts().forEach(orderProduct -> {
            products.stream()
                    .filter(p -> p.getId().equals(orderProduct.getId()))
                    .findFirst()
                    .ifPresentOrElse(businessProduct -> {
                        if (!businessProduct.isAvailable()) {
                            failureMessages.add("Product " + businessProduct.getName() + " is currently unavailable!");
                        }
                    }, () -> failureMessages.add("Product with id " + orderProduct.getId() + " does not belong to this business!"));
        });
    }

    public void constructOrderApproval(OrderApprovalStatus status) {
        this.orderApproval = new OrderApproval(
                new OrderApprovalId(UUID.randomUUID()),
                super.getId(),
                this.orderDetail != null ? this.orderDetail.getOrderId() : null,
                status
        );
    }

    private Business(Builder builder) {
        super.setId(builder.businessId);
        this.businessName = builder.businessName;
        this.businessContact = builder.businessContact;
        this.orderApproval = builder.orderApproval;
        this.active = builder.active != null ? builder.active : true;
        this.orderDetail = builder.orderDetail;
        this.products = builder.products != null ? new ArrayList<>(builder.products) : new ArrayList<>();
    }

    public BusinessName getBusinessName() { return businessName; }
    public BusinessContact getBusinessContact() { return businessContact; }
    public OrderApproval getOrderApproval() { return orderApproval; }
    public boolean isActive() { return active; }
    public OrderDetail getOrderDetail() { return orderDetail; }
    public List<Product> getProducts() { return Collections.unmodifiableList(products); }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private BusinessId businessId;
        private BusinessName businessName;
        private BusinessContact businessContact;
        private OrderApproval orderApproval;
        private Boolean active;
        private OrderDetail orderDetail;
        private List<Product> products;

        public Builder businessId(BusinessId businessId) {
            this.businessId = businessId;
            return this;
        }

        public Builder businessName(BusinessName businessName) {
            this.businessName = businessName;
            return this;
        }

        public Builder businessContact(BusinessContact businessContact) {
            this.businessContact = businessContact;
            return this;
        }

        public Builder orderApproval(OrderApproval orderApproval) {
            this.orderApproval = orderApproval;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder orderDetail(OrderDetail orderDetail) {
            this.orderDetail = orderDetail;
            return this;
        }

        public Builder products(List<Product> products) {
            this.products = products;
            return this;
        }

        public Business build() {
            return new Business(this);
        }
    }
}
