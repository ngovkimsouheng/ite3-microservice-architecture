//package kh.edu.istad.platform.customer.persistence.entity;
//
/// /import jakarta.persistence.Table;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.lang.annotation.Target;
//import java.util.UUID;
//
//@Getter
//@Setter
//@NoArgsConstructor
////@AllArgsConstructor
//@Entity
//@Table(name = "customer")
//public class CustomerEntity {
//    @Id
//    private UUID customerid;
//}

package kh.edu.istad.platform.customer.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class CustomerEntity {

    @Id
    @Column(name = "customer_id")
    private UUID customerId;

    @Column(nullable = false)
    private String username;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "given_name")
    private String givenName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;
}

