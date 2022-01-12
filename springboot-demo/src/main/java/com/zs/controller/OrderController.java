package com.zs.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zs.entity.Order;
import com.zs.service.OrderService;

@RestController
@RequestMapping("order")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;

	@GetMapping("")
	public List<Order> listPage() {
		log.info("listPage");
		return orderService.getAll();
	}

	@GetMapping("{id}")
	public Order infoPage(@PathVariable Long id) {
		log.info("infoPage");
		return orderService.getById(id);
	}

}
