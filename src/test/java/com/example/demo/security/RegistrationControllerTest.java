package com.example.demo.security;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.security.User.LoginProvider;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PasswordEncoder encoder;	
	
	@MockBean
	private UserRepository userRepository;
	
	@Test
	void testRegisterUserCreated201() throws Exception {
		String requestUrl = "/register";
		String requestJson = "{\"username\": \"test1\", \"password\": \"test123\", \"givenName\": \"Test 1\",\"familyName\": \"Test 1\"}";
		int expectedStatus = 201;
		String expectedJson = "";
										
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testRegisterUserExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/register";
		String requestJson = "{\"username\": \"test1\", \"password\": \"test123\", \"givenName\": \"Test 1\",\"familyName\": \"Test 1\"}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		User user = new User("test1",encoder.encode("test123"),LoginProvider.INTERNAL);
		user.setFamilyName("Test 1");
		user.setGivenName("Test 1");
		given(userRepository.findByUsername("test1")).willReturn(Optional.of(user));
										
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
}
