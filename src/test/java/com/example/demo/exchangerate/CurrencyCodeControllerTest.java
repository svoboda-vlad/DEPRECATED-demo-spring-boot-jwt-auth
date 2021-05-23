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

	@Test
	void testGetAllCurrencyCodesOk200() throws Exception {
		List<CurrencyCode> codesList = new ArrayList<CurrencyCode>();
		codesList.add(new CurrencyCode("EUR", "EMU", 1));
		
		given(this.currencyCodeRepository.findAll()).willReturn(codesList);
		
		String token = AuthenticationService.generateTokenWithHeader("user");
		
		this.mvc.perform(get("/currency-code").header("Authorization", token).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(200))
				.andExpect(content().json("[{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}]"));
	}

	@Test
	void testCreateCurrencyCodeCreated201() throws Exception {
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		given(this.currencyCodeRepository.save(code)).willReturn(code);		
		
		String token = AuthenticationService.generateTokenWithHeader("user");
		String currencyCodeJson = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		
		this.mvc.perform(post("/currency-code").content(currencyCodeJson).header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(201))
				.andExpect(content().json(currencyCodeJson));
	}
	
	@Test
	void testCreateCurrencyCodeBadRequest400() throws Exception {
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		given(this.currencyCodeRepository.save(code)).willReturn(code);		
		given(this.currencyCodeRepository.findByCurrencyCode("EUR")).willReturn(code);
		
		String token = AuthenticationService.generateTokenWithHeader("user");
		String sentJsonCurrencyCode = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		
		this.mvc.perform(post("/currency-code").content(sentJsonCurrencyCode).header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
				.andExpect(content().string(""));
	}	

	@Test
	void testGetCurrencyCodeOk200() throws Exception {
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		given(this.currencyCodeRepository.findById(1L)).willReturn(Optional.of(code));		
		
		String token = AuthenticationService.generateTokenWithHeader("user");
		String expectedJsonCurrencyCode = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		
		this.mvc.perform(get("/currency-code/1").header("Authorization", token).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(200))
				.andExpect(content().json(expectedJsonCurrencyCode));
	}
	
	@Test
	void testGetCurrencyCodeNotFound404() throws Exception {
		given(this.currencyCodeRepository.findById(1L)).willReturn(Optional.empty());		
		
		String token = AuthenticationService.generateTokenWithHeader("user");
		
		this.mvc.perform(get("/currency-code/1").header("Authorization", token).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(404))
				.andExpect(content().string(""));
	}	

}
