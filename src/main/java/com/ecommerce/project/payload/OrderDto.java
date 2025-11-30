package com.ecommerce.project.payload;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDto {
	
	private Long orderId;
	private String email;//user email
	private List<OrderItemDto> orderItems;
	private LocalDate orderDate;
	private PaymentDto payment;
	private Double totalAmount;
	private String orderStatus;
	private Long addressId;;

	
}
