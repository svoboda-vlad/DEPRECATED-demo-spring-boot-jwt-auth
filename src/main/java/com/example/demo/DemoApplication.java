package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.exchangerate.CurrencyCodeRepository;
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
	@Profile("dev")
	public CommandLineRunner dataLoader(CurrencyCodeRepository currencyCodeRepo, UserRepository userRepo, PasswordEncoder encoder) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				User user1 = new User("user1", encoder.encode("pass123"),LoginProvider.INTERNAL,"User 1","User 1");
				User user2 = new User("108564931079495851483", encoder.encode(""),LoginProvider.GOOGLE, "Google User 1", "Google User 1");
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