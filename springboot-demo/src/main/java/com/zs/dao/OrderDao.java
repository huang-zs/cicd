package com.zs.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zs.entity.Order;

@Repository
public class OrderDao {

	private Map<Long, Order> orderDb = new HashMap<>();

	public List<Order> findAll() {
		return new ArrayList<>(orderDb.values());
	}

	public Order findById(Long id) {
		return orderDb.get(id);
	}

	public int count() {
		return orderDb.values().size();
	}

}
