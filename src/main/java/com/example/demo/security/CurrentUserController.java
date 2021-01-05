package com.example.demo.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentUserController {
	
	private final UserRepository userRepo;
		
	public CurrentUserController(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@GetMapping("/current-user")
	public ResponseEntity<UserInfo> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();		
		User user = userRepo.findByUsername(authentication.getName());
		
		if (user == null) return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
		
		UserInfo userInfo = new UserInfo(authentication.getPrincipal().toString(), user.getLastLoginDateTime());
		return new ResponseEntity<UserInfo>(userInfo, HttpStatus.OK);
	}
	
}