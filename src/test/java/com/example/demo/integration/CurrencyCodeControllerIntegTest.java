package com.example.demo.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exchangerate.CurrencyCode;
import com.example.demo.exchangerate.CurrencyCodeRepository;
import com.example.demo.exchangerate.ExchangeRate;
import com.example.demo.exchangerate.ExchangeRateRepository;
import com.example.demo.security.AuthenticationService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@WithMockUser - not needed
class CurrencyCodeControllerIntegTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private CurrencyCodeRepository currencyCodeRepository;

	@Autowired
	private ExchangeRateRepository exchangeRateRepository;	
	
	private String generateAuthorizationHeader() {
		return "Bearer " + AuthenticationService.generateToken("user");
	}
	
	@BeforeEach
	void initData() {
		CurrencyCode currencyCode1 = new CurrencyCode("EUR", "EMU", 1);
		CurrencyCode currencyCode2 = new CurrencyCode("USD", "USA", 1);
		List<CurrencyCode> currencyCodes = new ArrayList<CurrencyCode>(Arrays.asList(currencyCode1, currencyCode2));		
		currencyCodeRepository.saveAll(currencyCodes);
		ExchangeRate exchangeRate1 = new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), currencyCode1);
		ExchangeRate exchangeRate2 = new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("21.669"), currencyCode2);
		List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>(Arrays.asList(exchangeRate1, exchangeRate2));
		exchangeRateRepository.saveAll(exchangeRates);
	}

	@Test
	void testGetAllCurrencyCodesOk200() throws Exception {
		String requestUrl = "/currency-code";
		int expectedStatus = 200;
		String expectedJson = "[{\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}," 
		+ "{\"currencyCode\":\"USD\",\"country\":\"USA\",\"rateQty\":1}]";
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void testCreateCurrencyCodeCreated201() throws Exception {
		String requestUrl = "/currency-code";
		String requestJson = "{\"id\":0,\"currencyCode\":\"GBP\",\"country\":\"Velká Británie\",\"rateQty\":1}";
		int expectedStatus = 201;
		String expectedJson = "{\"currencyCode\":\"GBP\",\"country\":\"Velká Británie\",\"rateQty\":1}";
				
		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader()).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testCreateCurrencyCodeExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/currency-code";
		String requestJson = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		int expectedStatus = 400;
		String expectedJson = "";
						
		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader()).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}	

	@Test
	void testGetCurrencyCodeOk200() throws Exception {
		CurrencyCode code = currencyCodeRepository.findByCurrencyCode("EUR");
		
		String requestUrl = "/currency-code/" + code.getId();
		int expectedStatus = 200;
		String expectedJson = "{\"id\":" + code.getId() + ",\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
						
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetCurrencyCodeNotFound404() throws Exception {
		String requestUrl = "/currency-code/9999";
		int expectedStatus = 404;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}

}
