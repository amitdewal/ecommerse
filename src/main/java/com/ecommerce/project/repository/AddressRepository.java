package com.ecommerce.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.project.model.Address;
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
