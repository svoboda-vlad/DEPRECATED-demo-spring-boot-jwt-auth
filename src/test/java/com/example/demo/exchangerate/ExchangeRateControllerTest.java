package com.example.demo.exchangerate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.security.AuthenticationService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class ExchangeRateControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ExchangeRateRepository exchangeRateRepository;
	
	private String generateAuthorizationHeader() {
		return "Bearer " + AuthenticationService.generateToken("user");
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

		given(this.exchangeRateRepository.findAll()).willReturn(ratesList);

		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
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

		given(this.exchangeRateRepository.save(rate)).willReturn(rate);

		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}

	/*
	 * @Test void testGetExchangeRatesByCurrencyCode() {
	 * fail("Not yet implemented"); }
	 * 
	 * @Test void testGetExchangeRatesByRateDate() { fail("Not yet implemented"); }
	 */

}