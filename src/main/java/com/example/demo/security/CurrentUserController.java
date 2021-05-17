package com.example.demo.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CurrentUserController {
	
	private static final String CURRENT_USER_URL = "/current-user";
	private final UserDetailsService userService;
	private final UserRepository userRepository;

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(CURRENT_USER_URL)
	public ResponseEntity<CurrentUser> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();		
		if (authentication == null) return new ResponseEntity<CurrentUser>(HttpStatus.NOT_FOUND);
		
		UserDetails userDetails = userService.loadUserByUsername(authentication.getName());
		User user = userRepository.findByUsername(userDetails.getUsername());
		CurrentUser userInfo = new CurrentUser(user.getUsername(), user.getLastLoginDateTime(), user.getPreviousLoginDateTime());
		return ResponseEntity.ok(userInfo);
	}
	
}