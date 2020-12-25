package com.example.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentUserController {

	@GetMapping("/current-user")
	public User getCurrentUser(Authentication auth) {
		return auth != null ? (User) auth.getPrincipal() : null;
	}
}
