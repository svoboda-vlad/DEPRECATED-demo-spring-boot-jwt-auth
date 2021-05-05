package com.example.demo.security;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CurrentUser {

	private final String username;
	private final LocalDateTime lastLoginDateTime;
	private final LocalDateTime previousLoginDateTime;
	
}
