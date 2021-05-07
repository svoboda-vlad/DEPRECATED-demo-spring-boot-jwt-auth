package com.example.demo.security;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class RegistrationUser {

	@NotNull
	@Size(min = 1, max = 50)
	@NonNull
	private String username;
	
	@NotNull
	@Size(min = 4, max = 100)
	@NonNull
	private String password;

	public User toUser(PasswordEncoder passwordEncoder) {
		return new User(username, passwordEncoder.encode(password));
	}

}