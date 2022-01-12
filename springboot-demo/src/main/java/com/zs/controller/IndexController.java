package com.zs.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 常见代码审查用例
 * 
 * @author Administrator
 *
 */
@RestController
public class IndexController {

	private static final Logger log = LoggerFactory.getLogger(IndexController.class);

	@RequestMapping("t1")
	public void t1() {
		int i = 1 / 0;
		log.info("{}", i);
	}

	/**
	 * e.printStackTrace();
	 * 
	 * @return
	 */
	@RequestMapping("t2")
	public void t2() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * null
	 * 
	 * @return
	 */
	@RequestMapping("t3")
	public void t3() {
		String string = null;
		int length = string.length();
		log.info("{}", length);
	}

	/**
	 * 没使用的变量
	 * 
	 * @return
	 */
	@RequestMapping("t4")
	public void t4() {
		String s = "";
	}

	/**
	 * 异常没关流
	 * 
	 * @return
	 */
	@RequestMapping("t5")
	public void t5() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("a.txt");
			fileOutputStream.write(1);
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
}
