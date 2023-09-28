package com.example.demo.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.controllers.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserControllerTest {
	@Autowired
	UserController userController;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CartRepository cartRepository;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		cartRepository = mock(CartRepository.class);
		bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
		userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
		setupUser();
	}

	private void setupUser() {
		User user = new User();
		Cart cart = new Cart();
		user.setId(0);
		user.setUsername("test");
		user.setPassword("testPassword");
		user.setCart(cart);
		when(userRepository.findByUsername("test")).thenReturn(user);
		when(userRepository.findById(0L)).thenReturn(Optional.of(user));
		when(userRepository.findByUsername("user")).thenReturn(null);
	}
	/**
	 * Test for creating a user with a valid request (HTTP 200 OK).
	 */
	@Test
	void testCreateUserWithValidRequest() {
		// Arrange
		when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("hashedPassword");
		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername("test");
		createUserRequest.setPassword("testPassword");
		createUserRequest.setConfirmPassword("testPassword");

		// Act: Perform the action being tested
		final ResponseEntity<User> response = userController.createUser(createUserRequest);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		User user = response.getBody();
		assertNotNull(user);
		assertEquals(0, user.getId());
		assertEquals("test", user.getUsername());
		assertEquals("hashedPassword", user.getPassword());
	}

	/**
	 * Test for finding a user by username that is not found (HTTP 404 Not Found).
	 */
	@Test
	public void testFindUserByUsernameNotFound() {
		// Act: Perform the action being tested
		final ResponseEntity<User> response = userController.findByUserName("user");

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}

	/**
	 * Test for finding a user by ID successfully (HTTP 200 OK).
	 */
	@Test
	public void testFindUserByIdSuccess() {
		// Act: Perform the action being tested
		final ResponseEntity<User> response = userController.findById(0L);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		User user = response.getBody();
		assertNotNull(user);
		assertEquals(0, user.getId());
	}

	/**
	 * Test for finding a user by username successfully (HTTP 200 OK).
	 */
	@Test
	public void testFindUserByUsernameSuccess() {
		// Act: Perform the action being tested
		final ResponseEntity<User> response = userController.findByUserName("test");

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		User user = response.getBody();
		assertNotNull(user);
		assertEquals("test", user.getUsername());
	}

	/**
	 * Test for finding a user by ID that is not found (HTTP 404 Not Found).
	 */
	@Test
	public void testFindUserByIdNotFound() {
		// Act: Perform the action being tested
		final ResponseEntity<User> response = userController.findById(1L);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}

}
