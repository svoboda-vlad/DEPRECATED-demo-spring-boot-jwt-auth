package com.example.demo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

import com.example.demo.exchangerate.CurrencyCode;
import com.example.demo.exchangerate.CurrencyCodeRepository;
import com.example.demo.exchangerate.ExchangeRate;
import com.example.demo.security.AuthenticationService;
import com.example.demo.security.Role;
import com.example.demo.security.RoleRepository;
import com.example.demo.security.User;
import com.example.demo.security.UserRegister;
import com.example.demo.security.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional - removed due to false positive tests (error in production: detached entity passed to persist)
//@WithMockUser - not needed
class CurrencyCodeControllerIntegTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PasswordEncoder encoder;	
	
	@Autowired
	private CurrencyCodeRepository currencyCodeRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;	
	
	private static final String USERNAME = "user1";
	private static final String PASSWORD = "pass123";
	private static final String ROLE_USER = "ROLE_USER";
	
	private String generateAuthorizationHeader(String username) {
		return "Bearer " + AuthenticationService.generateToken(username);
	}
	
	@BeforeEach
	void initData() {
		CurrencyCode currencyCode1 = new CurrencyCode("EUR", "EMU", 1);
		CurrencyCode currencyCode2 = new CurrencyCode("USD", "USA", 1);
		ExchangeRate exchangeRate1 = new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), currencyCode1);
		ExchangeRate exchangeRate2 = new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("21.669"), currencyCode2);
		currencyCode1.addExchangeRate(exchangeRate1);
		currencyCode2.addExchangeRate(exchangeRate2);
		List<CurrencyCode> currencyCodes = new ArrayList<CurrencyCode>(Arrays.asList(currencyCode1, currencyCode2));		
		currencyCodeRepository.saveAll(currencyCodes);
		
		UserRegister userRegister = new UserRegister(USERNAME, PASSWORD, "user", "user");
		User user = userRegister.toUserInternal(encoder);
		user = userRepository.save(user);
		Optional<Role> optRole = roleRepository.findByName(ROLE_USER);
		user.addRole(optRole.get());
		userRepository.save(user);
		
	}
	
	@AfterEach
	void cleanData() {
		currencyCodeRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void testGetAllCurrencyCodesOk200() throws Exception {
		String requestUrl = "/currency-code";
		int expectedStatus = 200;
		String expectedJson = "[{\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}," 
		+ "{\"currencyCode\":\"USD\",\"country\":\"USA\",\"rateQty\":1}]";
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void testCreateCurrencyCodeCreated201() throws Exception {
		String requestUrl = "/currency-code";
		String requestJson = "{\"id\":0,\"currencyCode\":\"GBP\",\"country\":\"Velká Británie\",\"rateQty\":1}";
		int expectedStatus = 201;
		String expectedJson = "{\"currencyCode\":\"GBP\",\"country\":\"Velká Británie\",\"rateQty\":1}";
				
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
						
		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}	

	@Test
	void testGetCurrencyCodeOk200() throws Exception {
		CurrencyCode code = currencyCodeRepository.findByCurrencyCode("EUR");
		
		String requestUrl = "/currency-code/" + code.getId();
		int expectedStatus = 200;
		String expectedJson = "{\"id\":" + code.getId() + ",\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
						
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetCurrencyCodeNotFound404() throws Exception {
		String requestUrl = "/currency-code/9999";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateCurrencyCodeOk200() throws Exception {
		CurrencyCode code = currencyCodeRepository.findByCurrencyCode("EUR");
		
		String requestUrl = "/currency-code/" + code.getId();		
		String requestJson = "{\"id\":" + code.getId() + ",\"currencyCode\":\"EUR\",\"country\":\"EMU2\",\"rateQty\":1}";
		int expectedStatus = 200;
		String expectedJson = requestJson;
						
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
						
		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testDeleteCurrencyCodeNoContent204() throws Exception {
		CurrencyCode code = currencyCodeRepository.findByCurrencyCode("EUR");
		
		String requestUrl = "/currency-code/" + code.getId();		
		String requestJson = "";
		int expectedStatus = 204;
		String expectedJson = "";
				
		this.mvc.perform(delete(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
	}
	
	@Test
	void testDeleteCurrencyCodeNotFoundBadRequest400() throws Exception {
		String requestUrl = "/currency-code/3";
		String requestJson = "";
		int expectedStatus = 400;
		String expectedJson = "";
						
		this.mvc.perform(delete(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
	}		

}
