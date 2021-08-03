package com.example.demo.exchangerate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
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
// @WithMockUser - not needed
class ExchangeRateControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ExchangeRateRepository exchangeRateRepository;
	
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
	void testGetAllExchangeRatesOk200() throws Exception {
		String requestUrl = "/exchange-rate";
		int expectedStatus = 200;

		String expectedJson = "[{\"id\":0,\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}},"
				+ "{\"id\":0,\"rateDate\":\"2021-04-15\",\"rate\":21.669,\"currencyCode\":{\"id\":0,\"currencyCode\":\"USD\",\"country\":\"USA\",\"rateQty\":1}}]";

		CurrencyCode currencyCode1 = new CurrencyCode("EUR", "EMU", 1);
		CurrencyCode currencyCode2 = new CurrencyCode("USD", "USA", 1);
		List<ExchangeRate> ratesList = new ArrayList<ExchangeRate>();
		ratesList.add(new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), currencyCode1));
		ratesList.add(new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("21.669"), currencyCode2));

		given(exchangeRateRepository.findAll()).willReturn(ratesList);

		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().json(expectedJson));
	}

	@Test
	void testCreateExchangeRateCreated201() throws Exception {
		String requestUrl = "/exchange-rate";
		String requestJson = "{\"id\":0,\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":1}}";
		int expectedStatus = 201;
		String expectedJson = requestJson;

		CurrencyCode code = new CurrencyCode();
		code.setId(1L);
		ExchangeRate rate = new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), code);

		given(exchangeRateRepository.save(rate)).willReturn(rate);

		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetExchangeRatesByCurrencyCodeOk200() throws Exception {
		String requestUrl = "/exchange-rate/currency-code/1";
		int expectedStatus = 200;
		String expectedJson = "[{\"id\":0,\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}},"
				+ "{\"id\":0,\"rateDate\":\"2021-04-16\",\"rate\":25.840,\"currencyCode\":{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}}]";
		
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		List<ExchangeRate> ratesList = new ArrayList<ExchangeRate>();
		ratesList.add(new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), code));
		ratesList.add(new ExchangeRate(LocalDate.of(2021, 4, 16), new BigDecimal("25.840"), code));
		
		given(currencyCodeRepository.findById(1L)).willReturn(Optional.of(code));
		given(exchangeRateRepository.findByCurrencyCode(code)).willReturn(ratesList);
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetExchangeRatesByCurrencyCodeNotFound404() throws Exception {
		String requestUrl = "/exchange-rate/currency-code/1";
		int expectedStatus = 404;
		String expectedJson = "";
				
		given(currencyCodeRepository.findById(1L)).willReturn(Optional.empty());
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetExchangeRatesByRateDateOk200() throws Exception {
		String requestUrl = "/exchange-rate/date/2021-04-15";
		int expectedStatus = 200;
		String expectedJson = "[{\"id\":0,\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}},"
				+ "{\"id\":0,\"rateDate\":\"2021-04-15\",\"rate\":21.669,\"currencyCode\":{\"id\":0,\"currencyCode\":\"USD\",\"country\":\"USA\",\"rateQty\":1}}]";
		
		CurrencyCode currencyCode1 = new CurrencyCode("EUR", "EMU", 1);
		CurrencyCode currencyCode2 = new CurrencyCode("USD", "USA", 1);
		List<ExchangeRate> ratesList = new ArrayList<ExchangeRate>();
		ratesList.add(new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), currencyCode1));
		ratesList.add(new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("21.669"), currencyCode2));
		
		given(exchangeRateRepository.findByRateDate(LocalDate.of(2021, 4, 15))).willReturn(ratesList);
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME)).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testUpdateExchangeRateOk200() throws Exception {
		String requestUrl = "/exchange-rate/3";
		String requestJson = "{\"id\":3,\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":1}}";
		int expectedStatus = 200;
		String expectedJson = requestJson;

		CurrencyCode code = new CurrencyCode();
		code.setId(1L);
		ExchangeRate rate = new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), code);
		rate.setId(3L);
		
		given(exchangeRateRepository.findById(3L)).willReturn(Optional.of(rate));
		given(exchangeRateRepository.save(rate)).willReturn(rate);

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
		
		given(exchangeRateRepository.findById(3L)).willReturn(Optional.empty());

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testDeleteExchangeRateNoContent204() throws Exception {
		String requestUrl = "/exchange-rate/3";
		String requestJson = "";
		int expectedStatus = 204;
		String expectedJson = "";

		CurrencyCode code = new CurrencyCode();
		code.setId(1L);
		ExchangeRate rate = new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), code);
		rate.setId(3L);
		
		given(exchangeRateRepository.findById(3L)).willReturn(Optional.of(rate));

		this.mvc.perform(delete(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}		
	
	@Test
	void testDeleteExchangeRateNotFoundBadRequest400() throws Exception {
		String requestUrl = "/exchange-rate/3";
		String requestJson = "";
		int expectedStatus = 400;
		String expectedJson = "";
		
		given(exchangeRateRepository.findById(3L)).willReturn(Optional.empty());

		this.mvc.perform(delete(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetExchangeRateOk200() throws Exception {
		String requestUrl = "/exchange-rate/3";
		String requestJson = "";
		int expectedStatus = 200;
		String expectedJson = "{\"id\":3,\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"id\":1,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}}";

		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		code.setId(1L);
		ExchangeRate rate = new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), code);
		rate.setId(3L);
		
		given(exchangeRateRepository.findById(3L)).willReturn(Optional.of(rate));

		this.mvc.perform(get(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader(USERNAME))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}	
	
}