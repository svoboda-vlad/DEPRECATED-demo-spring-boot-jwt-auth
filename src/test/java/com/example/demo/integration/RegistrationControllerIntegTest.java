package com.example.demo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.security.User;
import com.example.demo.security.User.LoginProvider;
import com.example.demo.security.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RegistrationControllerIntegTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PasswordEncoder encoder;	
	
	@Autowired
	private UserRepository userRepository;
	
	@BeforeEach
	void initData() {
		User user = new User("user321", encoder.encode("pass321"),LoginProvider.INTERNAL);
		userRepository.save(user);
	}	
	
	@Test
	void testRegisterUserCreated201() throws Exception {
		String requestUrl = "/register";
		String requestJson = "{\"username\": \"test1\", \"password\": \"test123\"}";
		int expectedStatus = 201;
		String expectedJson = "";
										
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testRegisterUserExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/register";
		String requestJson = "{\"username\": \"user321\", \"password\": \"test123\"}";
		int expectedStatus = 400;
		String expectedJson = "";
												
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
}
