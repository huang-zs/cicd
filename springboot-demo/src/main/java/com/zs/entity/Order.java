package com.zs.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Order {

	private Long id;

	private String name;

	private BigDecimal amount;

}
