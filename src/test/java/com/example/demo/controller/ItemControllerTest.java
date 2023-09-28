package com.example.demo.controller;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ItemControllerTest {
	@Autowired
	ItemController itemController;
	@Autowired
	ItemRepository itemRepository;

	@BeforeEach
	void setUp() {
		itemRepository = mock(ItemRepository.class);
		itemController = new ItemController(itemRepository);
		setupItem();
	}

	private void setupItem() {
		Item item = new Item();
		item.setId(1L);
		item.setName("Americano");
		BigDecimal price = BigDecimal.valueOf(2.99);
		item.setPrice(price);
		item.setDescription("Espresso with water");
		when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
		when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
		when(itemRepository.findByName("Americano")).thenReturn(Collections.singletonList(item));
	}

	/**
	 * Test for retrieving all items with valid data (HTTP 200 OK).
	 */
	@Test
	public void testGetAllItemsWithValidData() {
		// Act: Perform the action being tested
		ResponseEntity<List<Item>> response = itemController.getItems();

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		List<Item> items = response.getBody();
		assertNotNull(items);
		assertEquals(1, items.size());
	}

	/**
	 * Test for retrieving an item by ID with valid data (HTTP 200 OK).
	 */
	@Test
	public void testGetItemByIdWithValidData() {
		// Act: Perform the action being tested
		ResponseEntity<Item> response = itemController.getItemById(1L);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		Item item = response.getBody();
		assertNotNull(item);
	}

	/**
	 * Test for retrieving an item by ID with non-existent data (HTTP 404 Not Found).
	 */
	@Test
	public void testGetItemByIdWithNonExistentData() {
		// Act: Perform the action being tested
		ResponseEntity<Item> response = itemController.getItemById(2L);

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}

	/**
	 * Test for retrieving items by name with valid data (HTTP 200 OK).
	 */
	@Test
	public void testGetItemsByNameWithValidData() {
		// Act: Perform the action being tested
		ResponseEntity<List<Item>> response = itemController.getItemsByName("Americano");

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		List<Item> items = response.getBody();
		assertNotNull(items);
		assertEquals(1, items.size());
	}

	/**
	 * Test for retrieving items by name with non-existent data (HTTP 404 Not Found).
	 */
	@Test
	public void testGetItemsByNameWithNonExistentData() {
		// Act: Perform the action being tested
		ResponseEntity<List<Item>> response = itemController.getItemsByName("Square Widget");

		// Assert: Verify the results
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}

}
