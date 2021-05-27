package com.example.demo.security;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

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
	void testCreateCurrencyCodeOk200() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user3\",\"password\":\"password3\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		
		User user = new User("user3",encoder.encode("password3"));
		given(userRepository.findByUsername("user3")).willReturn(user);
		given(userDetailsService.loadUserByUsername("user3")).willReturn(user);
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));
	}
	
}