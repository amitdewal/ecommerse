package com.ecommerce.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.payload.OrderDto;
import com.ecommerce.project.payload.OrderRequestDto;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.util.AuthUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(
	    name = "Order API",
	    description = "Order creation, order tracking, purchase history, and order status management."
	)
@RestController
@RequestMapping("/api")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private AuthUtil authUtil;

	@PostMapping("/order/users/payments/{paymentMethod}")
	public ResponseEntity<OrderDto> orderProducts(@PathVariable String paymentMethod,
			@RequestBody OrderRequestDto orderRequestDto) {

		String emailId = authUtil.loggedInEmail();
		OrderDto order = orderService.placeOrder(emailId, orderRequestDto.getAddressId(), paymentMethod,
				orderRequestDto.getPgName(), orderRequestDto.getPgPaymentId(), orderRequestDto.getPgStatus(),
				orderRequestDto.getPgResponseMessage());
		return new ResponseEntity<>(order, HttpStatus.CREATED);

	}

}
