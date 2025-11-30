package com.ecommerce.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	

}
