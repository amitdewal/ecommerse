package com.ecommerce.project.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDto;
import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;

import jakarta.transaction.Transactional;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AuthUtil authUtil;

	@Override
	public CartDto addProductToCart(Long productId, Integer quantity) {
		// find existing cart or create one

		Cart cart = createCart();
		// retrieve product details
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		// perform validations

		CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

		if (cartItem != null) {
			throw new APIException("Prodcut " + product.getProductName() + "Already exists");
		}

		if (product.getQuantity() == 0) {
			throw new APIException(product.getProductName() + " is not available");
		}

		if (product.getQuantity() < quantity) {
			throw new APIException("Please, make and order of the " + product.getProductName()
					+ " less than or equal to the quantity " + product.getQuantity() + ".");
		}
		// create cart item

		CartItem newCartItem = new CartItem();
		newCartItem.setProduct(product);
		newCartItem.setCart(cart);
		newCartItem.setQuantity(quantity);
		newCartItem.setDiscount(product.getDiscount());
		newCartItem.setProductPrice(product.getSpecialPrice());

		// save cart item

		cartItemRepository.save(newCartItem);

		product.setQuantity(product.getQuantity());
		cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

		// save cart
		cartRepository.save(cart);

		// return updated cart

		CartDto cartDto = modelMapper.map(cart, CartDto.class);

		List<CartItem> cartItems = cart.getCartItems();

		Stream<ProductDto> productDtoStream = cartItems.stream().map(ItemEvent -> {
			ProductDto map = modelMapper.map(ItemEvent.getProduct(), ProductDto.class);
			map.setQuantity(ItemEvent.getQuantity());
			return map;

		});

		cartDto.setProducts(productDtoStream.toList());

		return cartDto;
	}

	private Cart createCart() {
		Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
		if (userCart != null) {
			return userCart;
		}
		// new cart create

		Cart cart = new Cart();
		cart.setTotalPrice(0.00);
		cart.setUser(authUtil.loggedInUser());
		Cart newCart = cartRepository.save(cart);
		return newCart;
	}

	@Override
	public List<CartDto> getAllCarts() {
		List<Cart> carts = cartRepository.findAll();
		if (carts.size() == 0) {
			throw new APIException("No cart exists");
		}

		List<CartDto> cartDtos = carts.stream().map(cart -> {
			CartDto cartDto = modelMapper.map(cart, CartDto.class);

			List<ProductDto> products = cart.getCartItems().stream().map(cartItem -> {
				ProductDto productDto = modelMapper.map(cartItem.getProduct(), ProductDto.class);
				productDto.setQuantity(cartItem.getQuantity());// set the qty from cartitem
				return productDto;
			}).toList();
			cartDto.setProducts(products);
			return cartDto;
		}

		).collect(Collectors.toList());
//		List<CartDto> cartDtos = carts.stream().map(cart -> {
//
//			CartDto cartDto = modelMapper.map(cart, CartDto.class);
//
//			List<ProductDto> productDtos = cart.getCartItems().stream()
//					.map(p -> modelMapper.map(p.getProduct(), ProductDto.class)).collect(Collectors.toList());
//
//			cartDto.setProducts(productDtos);
//			return cartDto;
//		}).collect(Collectors.toList());

		return cartDtos;
	}

	@Override
	public CartDto getCart(String emailId, Long cartId) {

		Cart cart = cartItemRepository.findCartByEmailAndCartId(emailId, cartId);
		if (cart == null) {
			throw new ResourceNotFoundException("Cart", "cartId", cartId);
		}

		CartDto cartDto = modelMapper.map(cart, CartDto.class);
		cart.getCartItems().forEach(cartItem -> cartItem.getProduct().setQuantity(cartItem.getQuantity()));
		List<ProductDto> productList = cart.getCartItems().stream()
				.map(p -> modelMapper.map(p.getProduct(), ProductDto.class)).toList();
		cartDto.setProducts(productList);
		return cartDto;
	}

	@Override
	@Transactional
	public CartDto updateProductQuantityInCart(Long productId, Integer quantity) {

		// find user and its cart
		String email = authUtil.loggedInEmail();
		Cart userCart = cartRepository.findCartByEmail(email);
		Long cartId = userCart.getCartId();

		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		if (product.getQuantity() == 0) {
			throw new APIException(product.getProductName() + " is not available");
		}

		if (product.getQuantity() < quantity) {
			throw new APIException("Please, make and order of the " + product.getProductName()
					+ " less than or equal to the quantity " + product.getQuantity() + ".");
		}

		CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
		if (cartItem == null) {
			throw new APIException("Product " + product.getProductName() + " not available in the cart!!");
		}

		// calculate new quantity
		Integer newQuantity = cartItem.getQuantity() + quantity;

		// validation to prevent negative quantitiees
		if (newQuantity < 0) {
			throw new APIException("The resulting quantity can not be negative");
		}

		if (newQuantity == 0) {
			String deleteProudctFromCart = deleteProudctFromCart(cartId, productId);
		} else {
			// update the item
			cartItem.setProductPrice(product.getSpecialPrice());
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
			cartItem.setDiscount(product.getDiscount());
			cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
			Cart save = cartRepository.save(cart);
		}

		CartItem updateditem = cartItemRepository.save(cartItem);
		if (updateditem.getQuantity() == 0) {
			cartItemRepository.deleteById(updateditem.getCartItemId());
		}

		CartDto cartDto = modelMapper.map(cart, CartDto.class);

		List<CartItem> cartItems = cart.getCartItems();

		Stream<ProductDto> productStream = cartItems.stream().map(ItemEvent -> {
			ProductDto productDto = modelMapper.map(ItemEvent.getProduct(), ProductDto.class);
			productDto.setQuantity(ItemEvent.getQuantity());
			return productDto;
		});

		cartDto.setProducts(productStream.toList());
		return cartDto;

	}

	@Transactional
	@Override
	public String deleteProudctFromCart(Long cartId, Long productId) {
		// validation
		Cart cart = cartRepository.findById(cartId)
				.orElseThrow((() -> new ResourceNotFoundException("Cart", "cartId", cartId)));

		CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

		if (cartItem == null) {
			throw new ResourceNotFoundException("Product", "productId", productId);

		}

		cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

		cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
		return "Product " + cartItem.getProduct().getProductName() + " removed from cart!!!";
	}

	@Override
	public void updateProductInCarts(Long cartId, Long productId) {
		// validations

		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

		if (cartItem == null) {
			throw new APIException("Product " + product.getProductName() + " not available in thr cart!!");

		}
		// 800 = 1000- 100 * 2 = 200
		double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

		// 200
		cartItem.setProductPrice(product.getSpecialPrice());

		// 800 _+ 200 * 2 = 1200
		cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

		cartItem = cartItemRepository.save(cartItem);

	}

}
