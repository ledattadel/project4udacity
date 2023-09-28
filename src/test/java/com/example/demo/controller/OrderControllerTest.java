package com.example.demo.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OrderControllerTest {
	@Autowired
	OrderController orderController;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	UserRepository userRepository;

	void setUp() {
		orderRepository = mock(OrderRepository.class);
		userRepository = mock(UserRepository.class);
		orderController = new OrderController(userRepository, orderRepository);
		setUpOrder();
	}

	public void setUpOrder() {
		Item item = new Item();
		item.setId(1L);
		item.setName("Americano");
		BigDecimal price = BigDecimal.valueOf(2.99);
		item.setPrice(price);
		item.setDescription("Espresso with water");
		List<Item> items = new ArrayList<Item>();
		items.add(item);

		User user = new User();
		Cart cart = new Cart();
		user.setId(0);
		user.setUsername("test");
		user.setPassword("testPassword");
		cart.setId(0L);
		cart.setUser(user);
		cart.setItems(items);
		BigDecimal total = BigDecimal.valueOf(2.99);
		cart.setTotal(total);
		user.setCart(cart);
		when(userRepository.findByUsername("test")).thenReturn(user);
		when(userRepository.findByUsername("user")).thenReturn(null);

	}

	/**
	 * Test for submitting an order with valid user (HTTP 200 OK).
	 */
	@Test
	public void testSubmitOrderWithValidUser() {
		// Arrange: Set up the test scenario
		setUp();

		// Act: Perform the action being tested
		ResponseEntity<UserOrder> response = orderController.submit("test");

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		UserOrder order = response.getBody();
		assertNotNull(order);
		assertEquals(1, order.getItems().size());
	}

	/**
	 * Test for submitting an order with non-existent user (HTTP 404 Not Found).
	 */
	@Test
	public void testSubmitOrderWithNonExistentUser() {
		// Arrange: Set up the test scenario
		setUp();

		// Act: Perform the action being tested
		ResponseEntity<UserOrder> response = orderController.submit("user");

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}

	/**
	 * Test for retrieving orders for a user with valid user (HTTP 200 OK).
	 */
	@Test
	public void testGetOrdersForUserWithValidUser() {
		// Arrange: Set up the test scenario
		setUp();

		// Act: Perform the action being tested
		ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("test");

		// Assert: Verify the results
		assertNotNull(ordersForUser);
		assertEquals(HttpStatus.OK.value(), ordersForUser.getStatusCodeValue());
		List<UserOrder> orders = ordersForUser.getBody();
		assertNotNull(orders);
	}

	/**
	 * Test for retrieving orders for a user with non-existent user (HTTP 404 Not Found).
	 */
	@Test
	public void testGetOrdersForUserWithNonExistentUser() {
		// Arrange: Set up the test scenario
		setUp();

		// Act: Perform the action being tested
		ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("user");

		// Assert: Verify the results
		assertNotNull(ordersForUser);
		assertEquals(HttpStatus.NOT_FOUND.value(), ordersForUser.getStatusCodeValue());
	}

}
