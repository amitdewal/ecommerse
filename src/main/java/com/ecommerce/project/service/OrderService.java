package com.ecommerce.project.service;

import com.ecommerce.project.payload.OrderDto;

import jakarta.transaction.Transactional;

public interface OrderService {
	@Transactional
	OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId,
			String pgStatus, String pgResponseMessage);

}
