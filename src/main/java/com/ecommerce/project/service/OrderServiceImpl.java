package com.ecommerce.project.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.model.Payment;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.OrderDto;
import com.ecommerce.project.payload.OrderItemDto;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.OrderItemRepository;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.PaymentRepository;
import com.ecommerce.project.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartService cartService;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId,
			String pgStatus, String pgResponseMessage) {

		// getting user cart
		Cart cart = cartRepository.findCartByEmail(emailId);
		if (cart == null) {
			throw new ResourceNotFoundException("Cart", "email", emailId);
		}

		Address address = addressRepository.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

		// create a new order with payment info
		Order order = new Order();
		order.setEmail(emailId);
		order.setOrderDate(LocalDate.now());
		order.setTotalAmount(cart.getTotalPrice());
		order.setOrderStatus("Order Accepted!");
		order.setAddress(address);

		Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
		payment.setOrder(order);
		payment = paymentRepository.save(payment);

		order.setPayment(payment);

		Order saveOrder = orderRepository.save(order);

		// get items from the cart and put into the oder items
		List<CartItem> cartItems = cart.getCartItems();

		if (cartItems.isEmpty()) {
			throw new APIException("Cart is empty");
		}

		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setDiscount(cartItem.getDiscount());
			orderItem.setOrderedProductPrice(cartItem.getProductPrice());
			orderItem.setOrder(saveOrder);

			orderItems.add(orderItem);

		}

		orderItems = orderItemRepository.saveAll(orderItems);
		
		// update product stock;
		cart.getCartItems().forEach(item -> {
			Integer quantity = item.getQuantity();
			Product product = item.getProduct();
			// Reduce stock quantity
			product.setQuantity(product.getQuantity() - quantity);
			 // Save product back to the database
			productRepository.save(product);

			// clear the cart;
			cartService.deleteProudctFromCart(cart.getCartId(), item.getProduct().getProductId());
		});

		// send back the order summary
		OrderDto orderDto = modelMapper.map(saveOrder, OrderDto.class);
		orderItems.forEach(item -> {
			orderDto.getOrderItems().add(modelMapper.map(item, OrderItemDto.class));
		});
		orderDto.setAddressId(addressId);

		return orderDto;
	}

}
