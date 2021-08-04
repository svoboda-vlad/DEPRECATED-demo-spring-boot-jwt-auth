package com.example.demo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.security.AuthenticationService;
import com.example.demo.security.Role;
import com.example.demo.security.RoleRepository;
import com.example.demo.security.User;
import com.example.demo.security.User.LoginProvider;
import com.example.demo.security.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional - removed due to false positive tests (error in production: detached entity passed to persist)
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
	
	private static final String USERNAME = "user1";
	private static final String PASSWORD = "pass123";	
	private static final String ROLE_USER = "ROLE_USER";
	private static final String USERNAME_INVALID = "user2";
	private static final String USERNAME_NEW = "usernew";
			
	private String generateAuthorizationHeader(String username) {
		return "Bearer " + AuthenticationService.generateToken(username);
	}
		
	@BeforeEach
	void initData() {
		User user = new User(USERNAME, encoder.encode(PASSWORD),LoginProvider.INTERNAL, "User 1", "User 1");
		Optional<Role> optRole = roleRepository.findByName(ROLE_USER);
		user = userRepository.save(user);
		user.addRole(optRole.get());
		userRepository.save(user);
	}
	
	@AfterEach
	void cleanData() {
		userRepository.deleteAll();
	}

	@Test
	void testGetCurrentUserOk200() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";
		
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetCurrentUserMissingAuthroizationHeaderNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetCurrentUserInvalidAuthroizationHeaderNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME_INVALID)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}	
	
	@Test
	void testGetCurrentUserInvalidTokenNotFound404() throws Exception {		
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME) + "xxx").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
		
	}
	
	@Test
	void testRegisterUserCreated201() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\": \"usernew\", \"password\": \"pass123new\",\"givenName\": \"Test 1\",\"familyName\": \"Test 1\"}";
		int expectedStatus = 201;
		String expectedJson = "";
		
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
		
		requestUrl = "/user";
		expectedStatus = 200;
		expectedJson = "{\"username\":\"usernew\",\"givenName\":\"Test 1\",\"familyName\":\"Test 1\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME_NEW))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
		
	}
	
	@Test
	void testRegisterUserExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\": \"user1\", \"password\": \"pass123\",\"givenName\": \"User 1\",\"familyName\": \"User 1\"}";
		int expectedStatus = 400;
		String expectedJson = "";
												
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateUserOk200() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\":\"user1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User X\",\"familyName\":\"User Y\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";
												
		this.mvc.perform(put(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testUpdateUserUsernameDoesNotMatchBadRequest400() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\":\"userx\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 400;
		String expectedJson = "";
														
		this.mvc.perform(put(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testDeleteUserNoContent204() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 204;
		String expectedJson = "";
		
		this.mvc.perform(delete(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}	
	
}