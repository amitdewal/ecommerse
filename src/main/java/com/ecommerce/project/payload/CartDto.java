package com.ecommerce.project.payload;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

	private Long cartId;
	private Double totalPriceDouble = 0.0;
	private List<ProductDto> products = new ArrayList<>();

}
