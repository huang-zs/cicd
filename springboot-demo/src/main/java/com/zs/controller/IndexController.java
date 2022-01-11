package com.zs.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	@RequestMapping("t1")
	public String t1() {
		int i = 1 / 0;
		return "" + i;
	}

	/**
	 * 测试e.printStackTrace();
	 * 
	 * @return
	 */
	@RequestMapping("t2")
	public String t2() {
		HashMap<String, String> hashMap = new HashMap<>();
		try {
			hashMap.get("null").length();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "t2";
	}

	/**
	 * 测试null校验
	 * 
	 * @return
	 */
	@RequestMapping("t3")
	public String t3() {
		HashMap<String, String> hashMap = new HashMap<>();
		String string = hashMap.get("null");
		string.length();
		return "t2";
	}

	/**
	 * 测试没使用的变量
	 * 
	 * @return
	 */
	@RequestMapping("t4")
	public String t4() {
		HashMap<String, String> hashMap = new HashMap<>();
		return "t4";
	}
}
