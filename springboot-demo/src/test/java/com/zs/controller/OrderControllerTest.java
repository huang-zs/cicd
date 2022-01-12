package com.zs.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OrderControllerTest {

	private TestRestTemplate testRestTemplate;

	@LocalServerPort
	private int port;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void before() {
		testRestTemplate = new TestRestTemplate(new RestTemplateBuilder().rootUri("http://localhost:" + port));
	}

	@Test
	void testListPage() {
		String orderList = testRestTemplate.getForObject("/order", String.class);
		assertNotNull(orderList);
	}

	@Test
	void testInfoPage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/order/1")).andExpect(MockMvcResultMatchers.status().isOk());
	}

}
