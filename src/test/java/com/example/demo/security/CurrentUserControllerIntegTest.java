package com.example.demo.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.example.demo.security.User.LoginProvider;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@WithMockUser - not needed
class CurrentUserControllerIntegTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private UserRepository userRepository;
	
	private String generateAuthorizationHeader() {
		return "Bearer " + AuthenticationService.generateToken("user321");
	}
	
	@BeforeEach
	void initData() {
		User user = new User("user321", encoder.encode("pass321"),LoginProvider.INTERNAL);
		userRepository.save(user);
	}	

	@Test
	void testGetCurrentUSerOk200() throws Exception {
		String requestUrl = "/current-user";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user321\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";
		
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetCurrentUserNotFound404() throws Exception {
		String requestUrl = "/current-user";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetCurrentUserInvalidTokenNotFound404() throws Exception {		
		String requestUrl = "/current-user";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader() + "xxx").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
		
	}	

}
