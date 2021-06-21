package com.example.demo.security;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CurrentUserController {
	
	private static final String CURRENT_USER_URL = "/current-user";
	private final UserDetailsService userService;
	private final UserRepository userRepository;

	@GetMapping(CURRENT_USER_URL)
	public ResponseEntity<CurrentUser> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) return new ResponseEntity<CurrentUser>(HttpStatus.NOT_FOUND);
		
		UserDetails userDetails = userService.loadUserByUsername(authentication.getName());
		Optional<User> optUser = userRepository.findByUsername(userDetails.getUsername());
		
		if (optUser.isEmpty()) return new ResponseEntity<CurrentUser>(HttpStatus.NOT_FOUND);
		
		User user = optUser.get();
		CurrentUser currentUser = new CurrentUser(user.getUsername(), user.getLastLoginDateTime(), user.getPreviousLoginDateTime(), user.getGivenName(), user.getFamilyName());
		return ResponseEntity.ok(currentUser);
	}
	
}