package com.example.demo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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
//@WithMockUser - not needed
class UserAdminControllerIntegTest {
	
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
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
			
	private String generateAuthorizationHeader(String username) {
		return "Bearer " + AuthenticationService.generateToken(username);
	}
		
	@BeforeEach
	void initData() {
		User user = new User(USERNAME, encoder.encode(PASSWORD),LoginProvider.INTERNAL, "User 1", "User 1");
		user = userRepository.save(user);
		Optional<Role> optRole1 = roleRepository.findByName(ROLE_USER);
		Optional<Role> optRole2 = roleRepository.findByName(ROLE_ADMIN);
		user.addRole(optRole1.get());
		user.addRole(optRole2.get());
		userRepository.save(user);
	}
	
	@AfterEach
	void cleanData() {
		userRepository.deleteAll();
	}

	@Test
	void testGetAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user2\",\"givenName\":\"User 2\",\"familyName\":\"User 2\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}]},"+
		"{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}},{\"role\":{\"name\":\"ROLE_ADMIN\"}}]}]";
		
		User user2 = new User("user2", StringUtils.repeat("A", 60), LoginProvider.INTERNAL, "User 2", "User 2");
		userRepository.save(user2);
		Optional<Role> optRole1 = roleRepository.findByName(ROLE_USER);
		user2.addRole(optRole1.get());
		userRepository.save(user2);
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}

}
