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
import com.example.demo.security.AuthenticationService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
// @WithMockUser - not needed
class ExchangeRateControllerIntegTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private CurrencyCodeRepository currencyCodeRepository;
	
	private String generateAuthorizationHeader() {
		return "Bearer " + AuthenticationService.generateToken("user");
	}
	
	@BeforeEach
	void initData() {
		CurrencyCode currencyCode1 = new CurrencyCode("EUR", "EMU", 1);
		CurrencyCode currencyCode2 = new CurrencyCode("USD", "USA", 1);
		currencyCode1.addExchangeRate(
				new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), currencyCode1)
				);
		currencyCode1.addExchangeRate(
				new ExchangeRate(LocalDate.of(2021, 4, 16), new BigDecimal("25.840"), currencyCode1)
				);
		currencyCode2.addExchangeRate(
				new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("21.669"), currencyCode2)
				);
		List<CurrencyCode> currencyCodes = new ArrayList<CurrencyCode>(Arrays.asList(currencyCode1, currencyCode2));
		currencyCodeRepository.saveAll(currencyCodes);
	}

	@Test
	void testGetAllExchangeRatesOk200() throws Exception {
		String requestUrl = "/exchange-rate";
		int expectedStatus = 200;

		String expectedJson = "[{\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}},"
				+ "{\"rateDate\":\"2021-04-15\",\"rate\":21.669,\"currencyCode\":{\"currencyCode\":\"USD\",\"country\":\"USA\",\"rateQty\":1}},"
				+ "{\"rateDate\":\"2021-04-16\",\"rate\":25.840,\"currencyCode\":{\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}}]";
		
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().json(expectedJson));
	}

	@Test
	void testCreateExchangeRateCreated201() throws Exception {
		CurrencyCode code = currencyCodeRepository.findByCurrencyCode("EUR");
		String requestUrl = "/exchange-rate";
		String requestJson = "{\"rateDate\":\"2021-04-17\",\"rate\":25.340,\"currencyCode\":{\"id\":" + code.getId() + "}}";
		int expectedStatus = 201;
		String expectedJson = requestJson;

		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader())
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
						
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetExchangeRatesByCurrencyCodeNotFound404() throws Exception {
		String requestUrl = "/exchange-rate/currency-code/9999";
		int expectedStatus = 404;
		String expectedJson = "";
						
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetExchangeRatesByRateDateOk200() throws Exception {
		String requestUrl = "/exchange-rate/2021-04-15";
		int expectedStatus = 200;
		String expectedJson = "[{\"rateDate\":\"2021-04-15\",\"rate\":25.940,\"currencyCode\":{\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}},"
				+ "{\"rateDate\":\"2021-04-15\",\"rate\":21.669,\"currencyCode\":{\"currencyCode\":\"USD\",\"country\":\"USA\",\"rateQty\":1}}]";
						
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
		
}