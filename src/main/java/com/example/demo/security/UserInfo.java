package com.example.demo.security;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserInfo {

	private final String username;
	private final LocalDateTime lastLoginDateTime;
	
}
