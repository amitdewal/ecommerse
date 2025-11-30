package com.ecommerce.project.controller;

import java.lang.invoke.StringConcatFactory;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDto;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(
	    name = "Cart API",
	    description = "Manage user's shopping cart including add, remove, and view cart items."
	)
@RestController
@RequestMapping("/api")
public class CartController {

	@Autowired
	private final CartRepository cartRepository;

	@Autowired
	private CartService cartService;

	@Autowired
	private AuthUtil authUtil;

	CartController(CartRepository cartRepository) {
		this.cartRepository = cartRepository;
	}

	@PostMapping("/carts/productId/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDto> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
		CartDto cartDto = cartService.addProductToCart(productId, quantity);
		System.out.println(cartDto);
		return new ResponseEntity<>(cartDto, HttpStatus.CREATED);
	}

	@GetMapping("/carts")
	public ResponseEntity<List<CartDto>> getAllCarts() {
		List<CartDto> cartDtos = cartService.getAllCarts();
		return new ResponseEntity<>(cartDtos, HttpStatus.FOUND);
	}

	@GetMapping("carts/users/cart")
	public ResponseEntity<CartDto> getCartById() {

		String emailId = authUtil.loggedInEmail();
		Cart cart = cartRepository.findCartByEmail(emailId);
		Long cartId = cart.getCartId();

		CartDto cartDto = cartService.getCart(emailId, cartId);
		return new ResponseEntity<>(cartDto, HttpStatus.OK);

	}

	@PutMapping("/cart/products/{productId}/quantity/{operation}")
	public ResponseEntity<CartDto> updateCartProduct(@PathVariable Long productId, @PathVariable String operation) {

		CartDto cartDto = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);

		return new ResponseEntity<>(cartDto,HttpStatus.OK);

	}
	
	// delete product from cart
	@DeleteMapping("/carts/{cartId}/product/{productId}")
	public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,@PathVariable
			Long productId){
		
	String status = cartService.deleteProudctFromCart(cartId,productId);
	
	return new ResponseEntity<>(status,HttpStatus.OK);
		
	}

}
