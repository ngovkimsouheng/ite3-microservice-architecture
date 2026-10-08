//package kh.edu.istad.platform.customer.persistence.adapter;
//
//import kh.edu.istad.platform.customer.domain.entity.Customer;
//import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
//import kh.edu.istad.platform.customer.persistence.entity.CustomerEntity;
//import kh.edu.istad.platform.customer.persistence.repository.CustomerJpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class CustomerRepositoryAdapter implements CustomerRepository {
//    private CustomerJpaRepository customerJpaRepository;
//
//    @Override
//    public Customer save(Customer customer) {
//        customerJpaRepository.save(new CustomerEntity());
//        return customer;
//    }
//}


package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.persistence.entity.CustomerEntity;
import kh.edu.istad.platform.customer.persistence.repository.CustomerJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;

    @Override
    public Customer save(Customer customer) {

        CustomerEntity customerEntity = new CustomerEntity();

        customerEntity.setCustomerId(
                customer.getId().value()
        );

        customerEntity.setUsername(
                customer.getUsername()
        );

        customerEntity.setFamilyName(
                customer.getFamilyName()
        );

        customerEntity.setGivenName(
                customer.getGivenName()
        );

        customerEntity.setEmail(
                customer.getEmail()
        );

        customerEntity.setPhoneNumber(
                customer.getPhoneNumber()
        );

        customerEntity.setStatus(
                customer.getStatus()
        );

        customerJpaRepository.save(customerEntity);

        return customer;
    }
}

