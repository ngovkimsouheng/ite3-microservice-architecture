package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public Customer execute(UpdateCustomerCommand command) {

        Customer customer = customerRepository
                .findById(command.customerId())
                .orElseThrow(() ->
                        new RuntimeException("Customer not found")
                );

        customer.updateCustomer(
                command.familyName(),
                command.givenName()
        );

        return customerRepository.save(customer);
    }
}