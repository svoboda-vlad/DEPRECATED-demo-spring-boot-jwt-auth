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
import com.example.demo.exchangerate.ExchangeRateRepository;
import com.example.demo.security.AuthenticationService;
import com.example.demo.security.Role;
import com.example.demo.security.RoleRepository;
import com.example.demo.security.User;
import com.example.demo.security.UserRegister;
import com.example.demo.security.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional - removed due to false positive tests (error in production: detached entity passed to persist)
// @WithMockUser - not needed
class ExchangeRateControllerIntegTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PasswordEncoder encoder;	

	@Autowired
	private CurrencyCodeRepository currencyCodeRepository;

	@Autowired
	private ExchangeRateRepository exchangeRateRepository;	
	
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
		ExchangeRate exchangeRate2 = new ExchangeRate(LocalDate.of(2021, 4, 16), new BigDecimal("25.840"), currencyCode1);		
		ExchangeRate exchangeRate3 = new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("21.669"), currencyCode2);
		currencyCode1.addExchangeRate(exchangeRate1);
		currencyCode1.addExchangeRate(exchangeRate2);
		currencyCode2.addExchangeRate(exchangeRate3);				
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
	void testGetAllExchangeRatesOk200() throws Exception {
		String requestUrl = "/exchange-rate";
		int expectedStatus = 200;

		String expectedJson = "[{\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}},"
				+ "{\"rateDate\":\"2021-04-15\",\"rate\":21.669,\"currencyCode\":{\"currencyCode\":\"USD\",\"country\":\"USA\",\"rateQty\":1}},"
				+ "{\"rateDate\":\"2021-04-16\",\"rate\":25.840,\"currencyCode\":{\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}}]";
		
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().json(expectedJson));
	}

	@Test
	void testCreateExchangeRateCreated201() throws Exception {
		CurrencyCode code = currencyCodeRepository.findByCurrencyCode("EUR");
		String requestUrl = "/exchange-rate";
		String requestJson = "{\"rateDate\":\"2021-04-17\",\"rate\":25.340,\"currencyCode\":{\"id\":" + code.getId() + "}}";
		int expectedStatus = 201;
		String expectedJson = requestJson;

		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetExchangeRatesByCurrencyCodeOk200() throws Exception {
		CurrencyCode code = currencyCodeRepository.findByCurrencyCode("EUR");
		String requestUrl = "/exchange-rate/currency-code/" + code.getId();
		int expectedStatus = 200;
		String expectedJson = "[{\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}},"
				+ "{\"rateDate\":\"2021-04-16\",\"rate\":25.840,\"currencyCode\":{\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}}]";
						
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetExchangeRatesByCurrencyCodeNotFound404() throws Exception {
		String requestUrl = "/exchange-rate/currency-code/9999";
		int expectedStatus = 404;
		String expectedJson = "";
						
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetExchangeRatesByRateDateOk200() throws Exception {
		String requestUrl = "/exchange-rate/date/2021-04-15";
		int expectedStatus = 200;
		String expectedJson = "[{\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}},"
				+ "{\"rateDate\":\"2021-04-15\",\"rate\":21.669,\"currencyCode\":{\"currencyCode\":\"USD\",\"country\":\"USA\",\"rateQty\":1}}]";
						
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testUpdateExchangeRateOk200() throws Exception {
		CurrencyCode code = currencyCodeRepository.findByCurrencyCode("EUR");
		List<ExchangeRate> ratesList = exchangeRateRepository.findByRateDate(LocalDate.of(2021, 4, 15));
		ExchangeRate rate = ratesList.get(0);
		String requestUrl = "/exchange-rate/" + rate.getId();
		
		String requestJson = "{\"id\":" + rate.getId() + ",\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":" + code.getId() + "}}";
		int expectedStatus = 200;
		String expectedJson = requestJson;

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testUpdateExchangeRateMissingIdBadRequest400() throws Exception {
		String requestUrl = "/exchange-rate/3";
		String requestJson = "{\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":1}}";
		int expectedStatus = 400;
		String expectedJson = "";

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateExchangeRateIdsNotMatchingBadRequest400() throws Exception {
		String requestUrl = "/exchange-rate/2";
		String requestJson = "{\"id\":3,\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":1}}";
		int expectedStatus = 400;
		String expectedJson = "";

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateExchangeRateNotFoundBadRequest400() throws Exception {
		String requestUrl = "/exchange-rate/3";
		String requestJson = "{\"id\":3,\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":1}}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testDeleteExchangeRateNoContent204() throws Exception {		
		CurrencyCode currencyCode1 = new CurrencyCode("test1", "test1", 1);
		ExchangeRate exchangeRate1 = new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), currencyCode1);
		currencyCode1.addExchangeRate(exchangeRate1);
		currencyCode1 = currencyCodeRepository.save(currencyCode1);
		
		String requestUrl = "/exchange-rate/" + exchangeRate1.getId();
		int expectedStatus = 204;
		String expectedJson = "";

		this.mvc.perform(delete(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}		
	
	@Test
	void testDeleteExchangeRateNotFoundBadRequest400() throws Exception {
		String requestUrl = "/exchange-rate/3";
		int expectedStatus = 400;
		String expectedJson = "";
		
		this.mvc.perform(delete(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetExchangeRateOk200() throws Exception {
		CurrencyCode code = currencyCodeRepository.findByCurrencyCode("EUR");
		List<ExchangeRate> ratesList = exchangeRateRepository.findByRateDate(LocalDate.of(2021, 4, 15));
		ExchangeRate rate = ratesList.get(0);
		
		String requestUrl = "/exchange-rate/" + rate.getId();
		int expectedStatus = 200;
		String expectedJson = "{\"id\":" + rate.getId() + ",\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":" + code.getId() + "}}";

		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}	
		
}