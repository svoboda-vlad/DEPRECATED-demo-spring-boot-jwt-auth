package com.example.demo;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.security.User;
import com.example.demo.security.UserRepository;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(UserRepository userRepo) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				String username = "108564931079495851483";
				if (userRepo.findByUsername(username) == null) {
					userRepo.save(new User(1, username, "svoboda.vladimir1@gmail.com", "Vladimir", "Svoboda", "cs", LocalDateTime.now()));					
				}
			}
		};
	}
}