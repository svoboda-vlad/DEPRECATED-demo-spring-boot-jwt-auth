package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.exchangerate.CurrencyCode;
import com.example.demo.exchangerate.CurrencyCodeRepository;
import com.example.demo.exchangerate.ExchangeRate;
import com.example.demo.security.User;
import com.example.demo.security.User.LoginProvider;
import com.example.demo.security.UserRepository;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;

@SpringBootApplication
public class DemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean
	@Profile("testdata")
	public CommandLineRunner dataLoader(CurrencyCodeRepository currencyCodeRepo, UserRepository userRepo, PasswordEncoder encoder) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				CurrencyCode currencyCode1 = new CurrencyCode("EUR", "EMU", 1);
				CurrencyCode currencyCode2 = new CurrencyCode("USD", "USA", 1);
				currencyCode1.addExchangeRate(
						new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("25.940"), currencyCode1)
						);
				currencyCode2.addExchangeRate(
						new ExchangeRate(LocalDate.of(2021, 4, 15), new BigDecimal("21.669"), currencyCode2)
						);
				List<CurrencyCode> currencyCodes = new ArrayList<CurrencyCode>(Arrays.asList(currencyCode1, currencyCode2));
				if (currencyCodeRepo.findAll().size() == 0) {
					currencyCodeRepo.saveAll(currencyCodes);
				}				
				
				User user1 = new User("user1", encoder.encode("pass123"),LoginProvider.INTERNAL);
				User user2 = new User("108564931079495851483", encoder.encode(""),LoginProvider.GOOGLE);
				List<User> users = new ArrayList<User>(Arrays.asList(user1, user2));				
				if(userRepo.findAll().size() == 0) {
					userRepo.saveAll(users);
				}
				
			}
		};
	}
	
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().components(new Components().addSecuritySchemes("bearer-key",
				new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
	}	

}