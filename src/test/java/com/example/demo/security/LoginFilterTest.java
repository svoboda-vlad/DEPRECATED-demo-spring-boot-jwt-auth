package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

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
class LoginFilterTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@MockBean
	UserDetailsService userDetailsService;
	
	@MockBean
	private UserRepository userRepository;

	@Test
	void testLoginNoLastLoginDateTimeOk200() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user3\",\"password\":\"password3\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		
		User user = new User("user3",encoder.encode("password3"), LoginProvider.INTERNAL);
		given(userRepository.findByUsername("user3")).willReturn(user);
		given(userDetailsService.loadUserByUsername("user3")).willReturn(user);
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));
		
		assertThat(user.getLastLoginDateTime()).isNotNull();
		assertThat(user.getPreviousLoginDateTime()).isNotNull();		
	}
	
	@Test
	void testLoginWithLastLoginDateTimeOk200() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user3\",\"password\":\"password3\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		
		User user = new User("user3",encoder.encode("password3"), LoginProvider.INTERNAL);
		LocalDateTime lastLoginDateTime = LocalDateTime.now();
		user.setLastLoginDateTime(lastLoginDateTime);
		given(userRepository.findByUsername("user3")).willReturn(user);
		given(userDetailsService.loadUserByUsername("user3")).willReturn(user);
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));
		
		assertThat(user.getLastLoginDateTime()).isAfter(lastLoginDateTime);
		assertThat(user.getPreviousLoginDateTime()).isEqualTo(lastLoginDateTime);
		
	}	
	
	@Test
	void testLoginWrongPasswordUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user3\",\"password\":\"wrongpassword\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		
		User user = new User("user3",encoder.encode("password3"), LoginProvider.INTERNAL);
		given(userRepository.findByUsername("user3")).willReturn(user);
		given(userDetailsService.loadUserByUsername("user3")).willReturn(user);
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}
	
	@Test
	void testLoginInvalidJsonUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"usernamexxx\":\"user3\",\"password\":\"password3\"}";
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
		String requestJson = "{\"username\":\"user3\",\"password\":\"password3\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		
		User user = new User("user3",encoder.encode("password3"), LoginProvider.GOOGLE);
		given(userRepository.findByUsername("user3")).willReturn(user);
		given(userDetailsService.loadUserByUsername("user3")).willReturn(user);
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}	
	
}