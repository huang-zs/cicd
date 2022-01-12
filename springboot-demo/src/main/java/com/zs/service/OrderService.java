package com.zs.service;

import java.util.List;

import com.zs.entity.Order;

public interface OrderService {

	List<Order> getAll();

	Order getById(Long id);

	String generateName();
}
