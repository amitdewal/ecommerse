package com.ecommerce.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

	private Long productId;
	@NotBlank
	@Size(min = 3, message = "Product name must contain atleast 3 characters")
	private String productName;
	@NotBlank
	@Size(min = 6, message = "Product description must contain atleast 6 characters")
	private String description;
	private String image;
	private Integer quantity;
	private double price;
	private double discount;
	private double specialPrice;
}
