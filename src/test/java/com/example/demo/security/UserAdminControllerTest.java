package com.example.demo.security;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.security.User.LoginProvider;

@SpringBootTest
@AutoConfigureMockMvc
//@WithMockUser - not needed
class UserAdminControllerTest {
	
	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private UserDetailsService userDetailsService;
	
	@MockBean
	private PasswordEncoder encoder;
	
	private static final String USERNAME = "user1";
	private static final String PASSWORD = "pass123";
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	
	private String generateAuthorizationHeader(String username) {
		return "Bearer " + AuthenticationService.generateToken(username);
	}
	
	@BeforeEach
	private void initData() {
		given(encoder.encode(PASSWORD)).willReturn(StringUtils.repeat("A", 60));
		UserRegister userRegister = new UserRegister(USERNAME, PASSWORD, "user", "user");
		User user = userRegister.toUserInternal(encoder);
		user.addRole(new Role(ROLE_USER));
		user.addRole(new Role(ROLE_ADMIN));
		given(userDetailsService.loadUserByUsername(USERNAME)).willReturn(user);
	}

	@Test
	void testgetAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}]},"+
		"{\"username\":\"user2\",\"givenName\":\"User 2\",\"familyName\":\"User 2\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}},{\"role\":{\"id\":0,\"name\":\"ROLE_ADMIN\"}}]}]";
		
		User user1 = new User("user1", StringUtils.repeat("A", 60), LoginProvider.INTERNAL, "User 1", "User 1");
		user1.addRole(new Role(ROLE_USER));
		User user2 = new User("user2", StringUtils.repeat("A", 60), LoginProvider.INTERNAL, "User 2", "User 2");
		user2.addRole(new Role(ROLE_USER));
		user2.addRole(new Role(ROLE_ADMIN));
		
		given(userRepository.findAll()).willReturn(new ArrayList<User>(List.of(user1, user2)));
		
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}

}
