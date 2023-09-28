package com.example.demo.controller;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CartControllerTest {
	@Autowired
	CartController cartController;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CartRepository cartRepository;
	@Autowired
	ItemRepository itemRepository;

	void setUp() {
		userRepository = mock(UserRepository.class);
		cartRepository = mock(CartRepository.class);
		itemRepository = mock(ItemRepository.class);
		cartController = new CartController(userRepository, cartRepository, itemRepository);
		setupCart();
	}

	private void setupCart() {
		User user = new User();
		Cart cart = new Cart();
		user.setId(0);
		user.setUsername("test");
		user.setPassword("testPassword");
		user.setCart(cart);
		when(userRepository.findByUsername("test")).thenReturn(user);

		Item item = new Item();
		item.setId(1L);
		item.setName("zzz");
		BigDecimal price = BigDecimal.valueOf(2.99);
		item.setPrice(price);
		item.setDescription("ccc");
		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

	}

	@Test
	public void testAddToCartWithValidUserAndItem() {
		// Arrange: Set up the test scenario
		setUp();
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("test");

		// Act: Perform the action being tested
		ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		Cart cart = response.getBody();
		assertNotNull(cart);
		assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());
	}

	@Test
	public void testAddToCartWithInvalidUser() {
		// Arrange: Set up the test scenario
		setUp();
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("username");

		// Act: Perform the action being tested
		ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}

	@Test
	public void testAddToCartWithNonExistentItem() {
		// Arrange: Set up the test scenario
		setUp();
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(2L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("test");

		// Act: Perform the action being tested
		ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}

	@Test
	public void testRemoveFromCartWithValidUserAndItem() {
		// Arrange: Set up the test scenario
		setUp();

		// Set up test by adding two items to the cart.
		ModifyCartRequest addToCartRequest = new ModifyCartRequest();
		addToCartRequest.setItemId(1L);
		addToCartRequest.setQuantity(2);
		addToCartRequest.setUsername("test");
		cartController.addToCart(addToCartRequest);

		ModifyCartRequest removeFromCartRequest = new ModifyCartRequest();
		removeFromCartRequest.setItemId(1L);
		removeFromCartRequest.setQuantity(1);
		removeFromCartRequest.setUsername("test");

		// Act: Perform the action being tested
		ResponseEntity<Cart> response = cartController.removeFromCart(removeFromCartRequest);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		Cart cart = response.getBody();
		assertNotNull(cart);
		assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());

	}

	@Test
	public void testRemoveFromCartWithInvalidUser() {
		// Arrange: Set up the test scenario
		setUp();
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("username");

		// Act: Perform the action being tested
		ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}

	@Test
	public void testRemoveFromCartWithNonExistentItem() {
		// Arrange: Set up the test scenario
		setUp();
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(2L);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("test");

		// Act: Perform the action being tested
		ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}

}
