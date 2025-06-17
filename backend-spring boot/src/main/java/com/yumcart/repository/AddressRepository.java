package com.yumcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yumcart.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
