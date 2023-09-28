package com.example.demo.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	private UserRepository userRepository;
	private CartRepository cartRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UserController(UserRepository userRepository, CartRepository cartRepository,
						  BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		log.debug("UserController.findByUserName: Commencing user search");
		User user = userRepository.findByUsername(username);
		log.info("Located user with username {} in the database.", username);
		log.debug("UserController.findByUserName: Search operation complete");
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		log.debug("UserController.createUser: Initiating user creation process");
		log.info("UserController.createUser: Username to be created: {}", createUserRequest.getUsername());
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if (createUserRequest.getPassword().length() < 8) {
			log.info("Password must be at least 8 characters long.");
			log.info("UserController.createUser: User creation process terminated");
			return ResponseEntity.badRequest().build();
		} else if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.info("Passwords do not match");
			log.info("UserController.createUser: User creation process terminated");
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		log.info("UserController.createUser: User {} has been successfully created.", createUserRequest.getUsername());
		log.debug("UserController.createUser: User creation process completed");
		return ResponseEntity.ok(user);
	}
	
}
