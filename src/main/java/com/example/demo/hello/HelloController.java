package com.example.demo.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
public class HelloController {
	
	@GetMapping("/hello")
	public Hello sayHello() {
		return new Hello("Hello World!");
	}
	
	@GetMapping("/hello-restricted")
	public Hello sayHelloRestricted() {
		return new Hello("Hello World! (restricted)");
	}	
	
	@Data
	@AllArgsConstructor
	private class Hello {
		private String text;
	}

}
