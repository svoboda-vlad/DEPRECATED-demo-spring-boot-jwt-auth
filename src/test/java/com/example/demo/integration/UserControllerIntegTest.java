package com.example.demo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.security.AuthenticationService;
import com.example.demo.security.Role;
import com.example.demo.security.RoleRepository;
import com.example.demo.security.User;
import com.example.demo.security.User.LoginProvider;
import com.example.demo.security.UserRepository;
import com.example.demo.security.UserRoles;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@WithMockUser - not needed
class UserControllerIntegTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;	
	
	private String generateAuthorizationHeader(String username) {
		return "Bearer " + AuthenticationService.generateToken(username);
	}	
	
	@BeforeEach
	void initData() {
		User user = new User("user321", encoder.encode("pass321"),LoginProvider.INTERNAL, "User 321", "User 321");
		List<UserRoles> userRoles = new ArrayList<UserRoles>();
		Optional<Role> optRole = roleRepository.findByName("ROLE_USER");
		userRoles.add(new UserRoles(user, optRole.get()));
		user.setUserRoles(userRoles);
		userRepository.save(user);
	}	

	@Test
	void testGetCurrentUserOk200() throws Exception {
		String requestUrl = "/current-user";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user321\",\"givenName\":\"User 321\",\"familyName\":\"User 321\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";
		
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader("user321")).accept(MediaType.APPLICATION_JSON))
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
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader("user321") + "xxx").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
		
	}
	
	@Test
	void testRegisterUserCreated201() throws Exception {
		String requestUrl = "/register";
		String requestJson = "{\"username\": \"test1\", \"password\": \"test123\",\"givenName\": \"Test 1\",\"familyName\": \"Test 1\"}";
		int expectedStatus = 201;
		String expectedJson = "";
		
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
		
		requestUrl = "/current-user";
		expectedStatus = 200;
		expectedJson = "{\"username\":\"test1\",\"givenName\":\"Test 1\",\"familyName\":\"Test 1\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader("test1"))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
		
	}
	
	@Test
	void testRegisterUserExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/register";
		String requestJson = "{\"username\": \"user321\", \"password\": \"test123\",\"givenName\": \"User 321\",\"familyName\": \"User 321\"}";
		int expectedStatus = 400;
		String expectedJson = "";
												
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateUserOk200() throws Exception {
		String requestUrl = "/update-user";
		String requestJson = "{\"username\":\"user321\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user321\",\"givenName\":\"User X\",\"familyName\":\"User Y\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";
												
		this.mvc.perform(post(requestUrl).header("Authorization", generateAuthorizationHeader("user321")).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}	
	

}
