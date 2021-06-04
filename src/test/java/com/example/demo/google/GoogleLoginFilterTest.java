package com.example.demo.google;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.security.User;
import com.example.demo.security.UserRepository;
import com.example.demo.security.User.LoginProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.webtoken.JsonWebSignature.Header;

@SpringBootTest
@AutoConfigureMockMvc
//@WithMockUser - not needed
class GoogleLoginFilterTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@MockBean
	UserDetailsService userDetailsService;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private GoogleIdTokenVerifier googleIdTokenVerifier;	

	@Test
	void testGoogleLoginOk200() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		
		Header header = new Header();
		Payload payload = new Payload();
		payload.setSubject("user3");
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);		
		User user = new User("user3",encoder.encode(""),LoginProvider.GOOGLE);
		given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);
		given(userRepository.findByUsername("user3")).willReturn(user);
		given(userDetailsService.loadUserByUsername("user3")).willReturn(user);
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));
	}
	
	@Test
	void testGoogleLoginInvalidIdTokenUnauthorized401() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"invalididtoken\"}";
		int expectedStatus = 401;
		String expectedJson = "";

		given(googleIdTokenVerifier.verify("invalididtoken")).willThrow(GeneralSecurityException.class);
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}	
	
	@Test
	void testGoogleLoginInvalidJsonUnauthorized401() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idTokenxxx\":\"abcdef\"}";
		int expectedStatus = 401;
		String expectedJson = "";
				
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}		
	
}