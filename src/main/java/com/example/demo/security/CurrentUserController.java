package com.example.demo.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentUserController {
	
	private final UserDetailsService userService;

	public CurrentUserController(UserDetailsService userService) {
		this.userService = userService;
	}	

	@GetMapping("/current-user")
	public ResponseEntity<UserInfo> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = userService.loadUserByUsername(authentication.getName());
		
		if (userDetails == null) return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
		
		UserInfo userInfo = new UserInfo(userDetails.getUsername());
		return new ResponseEntity<UserInfo>(userInfo, HttpStatus.OK);
	}
	
}