package com.example.demo.security;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
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
//@WithMockUser - not needed
class UserControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private PasswordEncoder encoder;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private UserService userService;
	
	private String generateAuthorizationHeader(String username) {
		return "Bearer " + AuthenticationService.generateToken(username);
	}

	@Test
	void testGetCurrentUSerOk200() throws Exception {
		String requestUrl = "/current-user";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user\",\"givenName\":\"User\",\"familyName\":\"User\",\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";
				
		given(encoder.encode("password")).willReturn(StringUtils.repeat("A", 60));
		
		User user = new User("user",encoder.encode("password"),LoginProvider.INTERNAL,"User","User");
		Role role = new Role("ROLE_USER");
		user.addRole(role);
		
		given(userRepository.findByUsername("user")).willReturn(Optional.of(user));
		
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader("user")).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetCurrentUserMissingAuthorizationHeaderNotFound404() throws Exception {
		String requestUrl = "/current-user";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetCurrentUserInvalidAuthorizationHeaderNotFound404() throws Exception {
		String requestUrl = "/current-user";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader("userx")).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetCurrentUserInvalidTokenNotFound404() throws Exception {		
		String requestUrl = "/current-user";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader("user") + "xxx").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
	}
	
	@Test
	void testRegisterUserCreated201() throws Exception {
		String requestUrl = "/register";
		String requestJson = "{\"username\":\"test1\",\"password\":\"test123\",\"givenName\":\"Test 1\",\"familyName\":\"Test 1\"}";
		int expectedStatus = 201;
		String expectedJson = "";
		
		given(encoder.encode("test123")).willReturn(StringUtils.repeat("A", 60));
		
		UserRegister userRegister = new UserRegister("test1", "test123","Test 1", "Test 1");
		User user = userRegister.toUserInternal(encoder);
		
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
		
		verify(userService, times(1)).registerUser(user);
	}
	
	@Test
	void testRegisterUserExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/register";
		String requestJson = "{\"username\": \"test1\", \"password\": \"test123\", \"givenName\": \"Test 1\",\"familyName\": \"Test 1\"}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		given(encoder.encode("test123")).willReturn(StringUtils.repeat("A", 60));
		
		User user = new User("test1",encoder.encode("test123"),LoginProvider.INTERNAL, "Test 1", "Test 1");
		willThrow(new EntityExistsException("User already exists.")).given(userService).registerUser(user);
										
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateUserOk200() throws Exception {
		String requestUrl = "/update-user";
		String requestJson = "{\"username\":\"user\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user\",\"givenName\":\"User X\",\"familyName\":\"User Y\",\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";		
		
		given(encoder.encode("password")).willReturn(StringUtils.repeat("A", 60));
		
		User user = new User("user",encoder.encode("password"),LoginProvider.INTERNAL,"User","User");
		Role role = new Role("ROLE_USER");
		user.addRole(role);
		
		UserInfo userInfo = new UserInfo("user", "User X", "User Y");		

		given(userService.updateUser(userInfo)).willReturn(userInfo.toUser(user));
										
		this.mvc.perform(post(requestUrl).header("Authorization", generateAuthorizationHeader("user")).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}	
	
	@Test
	void testUpdateUserUserDoesNotExistsBadRequest400() throws Exception {
		String requestUrl = "/update-user";
		String requestJson = "{\"username\":\"user\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		given(encoder.encode("password")).willReturn(StringUtils.repeat("A", 60));
		
		UserInfo userInfo = new UserInfo("user", "User X", "User Y");

		given(userService.updateUser(userInfo)).willThrow(new EntityNotFoundException());
										
		this.mvc.perform(post(requestUrl).header("Authorization", generateAuthorizationHeader("user")).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
		
	@Test
	void testUpdateUserUsernameDoesNotMatchBadRequest400() throws Exception {
		String requestUrl = "/update-user";
		String requestJson = "{\"username\":\"userx\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		given(encoder.encode("password")).willReturn(StringUtils.repeat("A", 60));
												
		this.mvc.perform(post(requestUrl).header("Authorization", generateAuthorizationHeader("user")).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}

}
