package com.lungesoft.spring.jpa.controller;

import com.lungesoft.spring.jpa.Application;
import com.lungesoft.spring.jpa.entity.Address;
import com.lungesoft.spring.jpa.entity.Customer;
import com.lungesoft.spring.jpa.repositoty.AddressRepository;
import com.lungesoft.spring.jpa.repositoty.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @GetMapping("/findAll")
    public void findAll() {
        LOGGER.info("start addresses with findAll()");
        for (Address address : addressRepository.findAll()) {
            for (Customer customer : address.getCustomers()) {
                LOGGER.info(" - customer {} with that address ", customer.getFirstName());
            }
        }
        LOGGER.info("end addresses with findAll()");
    }

    @GetMapping("/findMethod")
    public void customFindAll() {
        LOGGER.info("start customers with findByFirstName()");
        customerRepository.findByFirstName("Jack");
        LOGGER.info("end customers with findByFirstName()");
    }

    @GetMapping("/findById")
    public void findById() {
        LOGGER.info("start customer with findById()");
        customerRepository.findById(1L);
        LOGGER.info("end customer with findById()");
    }
}
