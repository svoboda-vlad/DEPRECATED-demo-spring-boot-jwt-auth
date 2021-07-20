package com.example.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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

import com.example.demo.security.User;
import com.example.demo.security.User.LoginProvider;
import com.example.demo.security.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional - removed due to false positive tests (error in production: detached entity passed to persist)
//@WithMockUser - not needed
class LoginFilterIntegTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@BeforeEach
	void initData() {
		User user = new User("user321", encoder.encode("pass321"),LoginProvider.INTERNAL, "User 321", "User 321");
		userRepository.save(user);
	}
	
	@AfterEach
	void cleanData() {
		userRepository.deleteAll();
	}	

	@Test
	void testLoginNoLastLoginDateTimeOk200() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user321\",\"password\":\"pass321\"}";
		int expectedStatus = 200;
		String expectedJson = "";
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));
		
		Optional<User> user = userRepository.findByUsername("user321");
		
		assertThat(user.get().getLastLoginDateTime()).isNotNull();
		assertThat(user.get().getPreviousLoginDateTime()).isNotNull();		
	}
	
	@Test
	void testLoginWithLastLoginDateTimeOk200() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user322\",\"password\":\"pass322\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		
		User userWithLastLogin = new User("user322", encoder.encode("pass322"),LoginProvider.INTERNAL, "User 322", "User 322");
		LocalDateTime lastLoginDateTime = LocalDateTime.now();
		userWithLastLogin.setLastLoginDateTime(lastLoginDateTime);
		userRepository.save(userWithLastLogin);		
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));
		
		Optional<User> user = userRepository.findByUsername("user322");
		
		assertThat(user.get().getLastLoginDateTime()).isAfter(lastLoginDateTime);
		assertThat(user.get().getPreviousLoginDateTime()).isEqualTo(lastLoginDateTime);
		
	}	
	
	@Test
	void testLoginWrongPasswordUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user321\",\"password\":\"wrongpassword\"}";
		int expectedStatus = 401;
		String expectedJson = "";
						
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}
	
	@Test
	void testLoginInvalidLoginProviderUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user323\",\"password\":\"pass323\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		
		User userWithGoogleLogin = new User("user323", encoder.encode("pass323"),LoginProvider.GOOGLE, "User 323", "User 323");
		LocalDateTime lastLoginDateTime = LocalDateTime.now();
		userWithGoogleLogin.setLastLoginDateTime(lastLoginDateTime);
		userRepository.save(userWithGoogleLogin);				
						
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}
	
	@Test
	void testLoginInvalidJsonUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"usernamex\":\"user321\",\"password\":\"pass321\"}";
		int expectedStatus = 401;
		String expectedJson = "";
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}
	
	@Test
	void testLoginUsernameDoesNotExistsUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user321x\",\"password\":\"pass321\"}";
		int expectedStatus = 401;
		String expectedJson = "";
						
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}	
		
}