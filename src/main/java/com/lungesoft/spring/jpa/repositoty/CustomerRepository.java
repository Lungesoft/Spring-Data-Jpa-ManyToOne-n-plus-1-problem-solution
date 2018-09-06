package com.lungesoft.spring.jpa.repositoty;

import com.lungesoft.spring.jpa.entity.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @EntityGraph("Customer")
    List<Customer> findByFirstName(String lastName);

}