package com.lungesoft.spring.jpa.repositoty;

import com.lungesoft.spring.jpa.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}