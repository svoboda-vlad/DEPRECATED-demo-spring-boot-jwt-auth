package com.example.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentUserController {

	
	@GetMapping("/current-user")
	public UserInfo getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return new UserInfo(authentication.getPrincipal().toString());
	}
	
}
