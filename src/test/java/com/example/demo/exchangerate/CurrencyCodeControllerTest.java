package com.example.demo.exchangerate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
class CurrencyCodeControllerTest {
	
	@Autowired
	private MockMvc mvc;

	@MockBean
	private CurrencyCodeRepository currencyCodeRepository;
	
	private String generateAuthorizationHeader() {
		return "Bearer " + AuthenticationService.generateToken("user");
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
		
		given(this.currencyCodeRepository.findAll()).willReturn(codesList);
		
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
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
		given(this.currencyCodeRepository.save(code)).willReturn(code);		
				
		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader()).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testCreateCurrencyCodeBadRequest400() throws Exception {
		String requestUrl = "/currency-code";
		String requestJson = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		given(this.currencyCodeRepository.save(code)).willReturn(code);		
		given(this.currencyCodeRepository.findByCurrencyCode("EUR")).willReturn(code);
				
		this.mvc.perform(post(requestUrl).content(requestJson).header("Authorization", generateAuthorizationHeader()).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}	

	@Test
	void testGetCurrencyCodeOk200() throws Exception {
		String requestUrl = "/currency-code/1";
		int expectedStatus = 200;
		String expectedJson = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		given(this.currencyCodeRepository.findById(1L)).willReturn(Optional.of(code));		
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testGetCurrencyCodeNotFound404() throws Exception {
		String requestUrl = "/currency-code/1";
		int expectedStatus = 404;
		String expectedJson = "";
		
		given(this.currencyCodeRepository.findById(1L)).willReturn(Optional.empty());		
				
		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}

}
