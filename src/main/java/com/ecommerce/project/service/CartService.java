package com.ecommerce.project.service;

import java.util.List;

import com.ecommerce.project.payload.CartDto;

import jakarta.transaction.Transactional;

public interface CartService {

	public CartDto addProductToCart(Long productId, Integer quantity);

	public List<CartDto> getAllCarts();

	public CartDto getCart(String emailId, Long cartId);

	@Transactional
	public CartDto updateProductQuantityInCart(Long productId, Integer i);

	
	public String deleteProudctFromCart(Long cartId, Long productId);

	public void updateProductInCarts(Long cartId, Long productId);

}
