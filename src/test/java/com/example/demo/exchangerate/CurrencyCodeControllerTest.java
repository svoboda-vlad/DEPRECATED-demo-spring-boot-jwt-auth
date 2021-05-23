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
	void testGetAllCurrencyCodes() throws Exception {
		List<CurrencyCode> codesList = new ArrayList<CurrencyCode>();
		codesList.add(new CurrencyCode("EUR", "EMU", 1));
		
		given(this.currencyCodeRepository.findAll()).willReturn(codesList);
		
		String token = AuthenticationService.generateTokenWithHeader("user");
		
		this.mvc.perform(get("/currency-code").header("Authorization", token).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
				.andExpect(content().json("[{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}]"));
	}

	@Test
	void testCreateCurrencyCode() throws Exception {
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		given(this.currencyCodeRepository.save(code)).willReturn(code);		
		
		String token = AuthenticationService.generateTokenWithHeader("user");
		String currencyCodeJson = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		
		this.mvc.perform(post("/currency-code").content(currencyCodeJson).header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
				.andExpect(content().json(currencyCodeJson));
	}

	@Test
	void testGetCurrencyCode() throws Exception {
		CurrencyCode code = new CurrencyCode("EUR", "EMU", 1);
		given(this.currencyCodeRepository.findById(1L)).willReturn(Optional.of(code));		
		
		String token = AuthenticationService.generateTokenWithHeader("user");
		String currencyCodeJson = "{\"id\":0,\"currencyCode\":\"EUR\",\"country\":\"EMU\",\"rateQty\":1}";
		
		this.mvc.perform(get("/currency-code/1").header("Authorization", token).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
				.andExpect(content().json(currencyCodeJson));
	}

}
