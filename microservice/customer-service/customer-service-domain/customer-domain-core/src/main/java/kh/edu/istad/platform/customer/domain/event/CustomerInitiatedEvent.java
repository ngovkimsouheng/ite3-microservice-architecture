package kh.edu.istad.platform.customer.domain.event;

import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.service.CustomerDomainService;

import java.time.ZonedDateTime;

public class CustomerInitiatedEvent implements DomainEvent<Customer> {


    /**
     * event that happened cannot be modified, so we can make it final and immutable
     * */

    private final Customer customer;
    private final ZonedDateTime initiatedAt; //history of when the event was initiated

    public Customer getCustomer() {
        return customer;
    }

    public ZonedDateTime getInitiatedAt() {
        return initiatedAt;
    }

    public CustomerInitiatedEvent(Customer customer, ZonedDateTime initiatedAt) {
        this.customer = customer;
        this.initiatedAt = initiatedAt;
    }
}
