package com.example.demo.security;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	
	private static final String REGISTRATION_URL = "/register";
	private static final String CURRENT_USER_URL = "/current-user";
	private static final String UPDATE_USER_URL = "/update-user";
	private final UserRepository userRepository;
	private final UserDetailsService userService;
	private final PasswordEncoder encoder;
    
    @PostMapping(REGISTRATION_URL)
    public ResponseEntity<UserInfo> registerUser(@Valid @RequestBody UserRegister userRegister) {
		if (userRepository.findByUsername(userRegister.getUsername()).isPresent())
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		
		User user = userRepository.save(userRegister.toUserInternal(encoder));    	
		return ResponseEntity.status(HttpStatus.CREATED).body(user.toUserInfo());
    }

	@GetMapping(CURRENT_USER_URL)
	public ResponseEntity<UserInfo> getUserInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
		
		UserDetails userDetails = userService.loadUserByUsername(authentication.getName());
		Optional<User> optUser = userRepository.findByUsername(userDetails.getUsername());
		
		if (optUser.isEmpty()) return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
		
		User user = optUser.get();
		return ResponseEntity.ok(user.toUserInfo());
	}
	
    @PostMapping(UPDATE_USER_URL)
    public ResponseEntity<UserInfo> updateUser(@Valid @RequestBody UserInfo userInfo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
		
		UserDetails userDetails = userService.loadUserByUsername(authentication.getName());
		Optional<User> optUser = userRepository.findByUsername(userDetails.getUsername());
		
		if (optUser.isEmpty()) return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
		
		User user = optUser.get();
		
		user = userRepository.save(userInfo.toUser(user));
		return ResponseEntity.ok(user.toUserInfo());
    }

}