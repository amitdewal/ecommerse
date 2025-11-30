package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderRequestDto {

	private Long addressId;
	private String paymentMethod;
	private String pgName; // pg -> payment gateway
	private String pgPaymentId;// pg -> payment gateway
	private String pgStatus;
	private String pgResponseMessage;

}
