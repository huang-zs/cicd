package com.zs.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zs.dao.OrderDao;
import com.zs.entity.Order;
import com.zs.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao orderDao;

	@Override
	public List<Order> getAll() {
		return orderDao.findAll();
	}

	@Override
	public Order getById(Long id) {
		return orderDao.findById(id);
	}

	@Override
	public String generateName() {
		String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		int count = orderDao.count();
		return format + String.format("%05d", count);
	}

}
