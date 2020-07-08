package com.example.demo.hello;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.ArrayList;

@RestController
public class HelloController {

	@RequestMapping("/hello")
	public List<Hello> showHello() {
		return this.getHello();
	}
	
	private List<Hello> getHello() {
		List<Hello> helloList = new ArrayList<Hello>();
		helloList.add(new Hello(1, "Hello World 1!"));
		helloList.add(new Hello(2, "Hello World 2!"));		
		return helloList;
	}

}
