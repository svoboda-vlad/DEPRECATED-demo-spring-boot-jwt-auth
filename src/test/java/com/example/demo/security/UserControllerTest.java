package com.example.demo.security;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.security.User.LoginProvider;

@SpringBootTest
@AutoConfigureMockMvc
//@WithMockUser - not needed
class UserControllerTest {

	@Autowired
	private MockMvc mvc;
		
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private UserDetailsService userDetailsService;
	
	@MockBean
	private PasswordEncoder encoder;
	
	private static final String USERNAME = "user1";
	private static final String ROLE_USER = "ROLE_USER";
	private static final String USERNAME_INVALID = "user2";
	private static final String USERNAME_NEW = "usernew";
	private static final String PASSWORD_NEW = "pass123new";
	
	private String generateAuthorizationHeader(String username) {
		return "Bearer " + AuthenticationService.generateToken(username);
	}
	
	@Test
	void testGetCurrentUserOk200() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";
		
		User user = new User(USERNAME, "A".repeat(60), LoginProvider.INTERNAL, "User 1", "User 1");
		user.addRole(new Role(ROLE_USER));
		
		given(userDetailsService.loadUserByUsername(USERNAME)).willReturn(user);
		given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));
		
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetCurrentUserMissingAuthorizationHeaderNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetCurrentUserInvalidAuthorizationHeaderNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";
		
		given(userDetailsService.loadUserByUsername(USERNAME_INVALID)).willThrow(new UsernameNotFoundException("User not found."));
				
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
		String requestJson = "{\"username\":\"usernew\",\"password\":\"pass123new\",\"givenName\":\"New User\",\"familyName\":\"New User\"}";
		int expectedStatus = 201;
		String expectedJson = "";
		
		given(encoder.encode(PASSWORD_NEW)).willReturn("A".repeat(60));
		UserRegister userRegister = new UserRegister(USERNAME_NEW, PASSWORD_NEW, "New User", "New User");
		User user = userRegister.toUserInternal(encoder);
		
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
		
		verify(userService, times(1)).registerUser(user);
	}
	
	@Test
	void testRegisterUserExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\": \"usernew\", \"password\": \"pass123new\", \"givenName\": \"New User\",\"familyName\": \"New User\"}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		given(encoder.encode(PASSWORD_NEW)).willReturn("A".repeat(60));
		UserRegister userRegister = new UserRegister(USERNAME_NEW, PASSWORD_NEW, "New User", "New User");
		User user = userRegister.toUserInternal(encoder);
		
		given(userService.registerUser(user)).willThrow(new EntityExistsException("User already exists."));
										
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateUserOk200() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\":\"user1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User X\",\"familyName\":\"User Y\",\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";		
		
		User user = new User(USERNAME, "A".repeat(60), LoginProvider.INTERNAL, "User 1", "User 1");
		user.addRole(new Role(ROLE_USER));
		
		given(userDetailsService.loadUserByUsername(USERNAME)).willReturn(user);
		
		UserInfo userInfo = new UserInfo(USERNAME, "User X", "User Y");		

		given(userService.updateUser(userInfo)).willReturn(userInfo.toUser(user));
										
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
		
		User user = new User(USERNAME, "A".repeat(60), LoginProvider.INTERNAL, "User 1", "User 1");
		user.addRole(new Role(ROLE_USER));
		
		given(userDetailsService.loadUserByUsername(USERNAME)).willReturn(user);		
		
		this.mvc.perform(put(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testDeleteUserNoContent204() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 204;
		String expectedJson = "";
		
		User user = new User(USERNAME, "A".repeat(60), LoginProvider.INTERNAL, "User 1", "User 1");
		user.addRole(new Role(ROLE_USER));
		user.setId(1L);
		
		given(userDetailsService.loadUserByUsername(USERNAME)).willReturn(user);
		given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));
		
		this.mvc.perform(delete(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
		
		verify(userRepository, times(1)).deleteById(1L);
	}
	
}
