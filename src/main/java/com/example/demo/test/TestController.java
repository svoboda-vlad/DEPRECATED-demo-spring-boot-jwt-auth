package com.example.demo.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
public class TestController {
	
	@GetMapping("/test")
	public Test test() {
		return new Test(1, "test 123");
	}
	
	@Data
	@AllArgsConstructor
	private class Test {
		private int id;
		private String text;
	}

}
