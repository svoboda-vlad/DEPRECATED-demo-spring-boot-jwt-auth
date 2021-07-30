package com.example.demo.exchangerate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.example.demo.security.AuthenticationService;
import com.example.demo.security.Role;
import com.example.demo.security.User;
import com.example.demo.security.UserRegister;

@SpringBootTest
@AutoConfigureMockMvc
//@WithMockUser - not needed
class CurrencyCodeControllerTest {
	
	@Autowired
	private MockMvc mvc;

	@MockBean
	private CurrencyCodeRepository currencyCodeRepository;
	
	@MockBean
	private UserDetailsService userDetailsService;
	
	@MockBean
	private PasswordEncoder encoder;
	
	private static final String USERNAME = "user1";
	private static final String PASSWORD = "pass123";
	private static final String ROLE_USER = "ROLE_USER";
	
	private String generateAuthorizationHeader(String username) {
		return "Bearer " + AuthenticationService.generateToken(username);
	}
	
	@BeforeEach
	private void initData() {
		given(encoder.encode(PASSWORD)).willReturn(StringUtils.repeat("A", 60));
		UserRegister userRegister = new UserRegister(USERNAME, PASSWORD, "user", "user");
		User user = userRegister.toUserInternal(encoder);
		user.addRole(new Role(ROLE_USER));
		given(userDetailsService.loadUserByUsername(USERNAME)).willReturn(user);
	}

	@Test
	void testGetAllCurrencyCodesOk200() throws Exception {
		String requestUrl = "/currency-code";
		int expectedStatus = 200;
		String expectedJson = "[{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}," 
		+ "{\"id\":0,\"currencyCode\":\"USD\",\"country\":\"USA\",\"rateQty\":1}]";
		
		List<CurrencyCode> codesList = new ArrayList<CurrencyCode>();
		codesList.add(new CurrencyCode("EUR", "EMU", 1));
		codesList.add(new CurrencyCode("USD", "USA", 1));
		
		given(currencyCodeRepository.findAll()).willReturn(codesList);
		
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void testCreateCurrencyCodeCreated201() throws Exception {
		String requestUrl = "/currency-code";
		String requestJson = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		int expectedStatus = 201;
		String expectedJson = requestJson;
		
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		given(currencyCodeRepository.save(code)).willReturn(code);		
				
		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testCreateCurrencyCodeExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/currency-code";
		String requestJson = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		given(currencyCodeRepository.save(code)).willReturn(code);		
		given(currencyCodeRepository.findByCurrencyCode("EUR")).willReturn(code);
				
		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}	

	@Test
	void testGetCurrencyCodeOk200() throws Exception {
		String requestUrl = "/currency-code/1";
		int expectedStatus = 200;
		String expectedJson = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		given(currencyCodeRepository.findById(1L)).willReturn(Optional.of(code));		
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetCurrencyCodeNotFound404() throws Exception {
		String requestUrl = "/currency-code/1";
		int expectedStatus = 404;
		String expectedJson = "";
		
		given(currencyCodeRepository.findById(1L)).willReturn(Optional.empty());		
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateCurrencyCodeOk200() throws Exception {
		String requestUrl = "/currency-code/3";
		String requestJson = "{\"id\":3,\"currencyCode\":\"EUR\",\"country\":\"EMU2\",\"rateQty\":1}";
		int expectedStatus = 200;
		String expectedJson = requestJson;
		
		CurrencyCode code = new CurrencyCode("EUR", "EMU2", 1);
		code.setId(3);
		given(currencyCodeRepository.findById(3L)).willReturn(Optional.of(code));
		given(currencyCodeRepository.save(code)).willReturn(code);
				
		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testUpdateCurrencyCodeMissingIdBadRequest400() throws Exception {
		String requestUrl = "/currency-code/3";
		String requestJson = "{\"currencyCode\":\"EUR\",\"country\":\"EMU2\",\"rateQty\":1}";
		int expectedStatus = 400;
		String expectedJson = "";
						
		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateCurrencyCodeIdsNotMatchingBadRequest400() throws Exception {
		String requestUrl = "/currency-code/2";
		String requestJson = "{\"id\":3,\"currencyCode\":\"EUR\",\"country\":\"EMU2\",\"rateQty\":1}";
		int expectedStatus = 400;
		String expectedJson = "";
						
		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateCurrencyCodeNotFoundBadRequest400() throws Exception {
		String requestUrl = "/currency-code/3";
		String requestJson = "{\"id\":3,\"currencyCode\":\"EUR\",\"country\":\"EMU2\",\"rateQty\":1}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		given(currencyCodeRepository.findById(3L)).willReturn(Optional.empty());
				
		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testDeleteCurrencyCodeNoContent204() throws Exception {
		String requestUrl = "/currency-code/3";
		String requestJson = "";
		int expectedStatus = 204;
		String expectedJson = "";
		
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		code.setId(3);
		given(currencyCodeRepository.findById(3L)).willReturn(Optional.of(code));
				
		this.mvc.perform(delete(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
		
		verify(currencyCodeRepository, times(1)).deleteById(3L);
	}
	
	@Test
	void testDeleteCurrencyCodeNotFoundBadRequest400() throws Exception {
		String requestUrl = "/currency-code/3";
		String requestJson = "";
		int expectedStatus = 400;
		String expectedJson = "";
		
		given(currencyCodeRepository.findById(3L)).willReturn(Optional.empty());
				
		this.mvc.perform(delete(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
	}	
	
}