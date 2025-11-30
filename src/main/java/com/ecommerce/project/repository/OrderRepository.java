package com.ecommerce.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
