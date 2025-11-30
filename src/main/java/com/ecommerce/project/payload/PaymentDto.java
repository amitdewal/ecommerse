package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentDto {

	private Long paymenetId;
	private String paymentMethod;
	private String pgPaymentId;// pg -> payment gateway
	private String pgStatus;
	private String pgResponseMessage;
	private String pgName;

}
