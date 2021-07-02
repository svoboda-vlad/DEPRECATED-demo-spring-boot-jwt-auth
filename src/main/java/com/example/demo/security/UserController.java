package com.example.demo.security;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.example.demo.google.GoogleLoginFilter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	
	private final Logger log = LoggerFactory.getLogger(GoogleLoginFilter.class);
	
	private static final String REGISTRATION_URL = "/register";
	private static final String CURRENT_USER_URL = "/current-user";
	private static final String UPDATE_USER_URL = "/update-user";
	private static final String USER_ROLE_NAME = "ROLE_USER";
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserRolesRepository userRolesRepository;
	private final UserDetailsService userService;
	private final PasswordEncoder encoder;
    
    @PostMapping(REGISTRATION_URL)
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegister userRegister) {
		if (userRepository.findByUsername(userRegister.getUsername()).isPresent())
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		
		Optional<Role> optRole = roleRepository.findByName(USER_ROLE_NAME);
		
		if (optRole.isEmpty()) {
			log.info("Role {} not found in database.", USER_ROLE_NAME);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} else {
			User user = userRepository.save(userRegister.toUserInternal(encoder));
			UserRoles userRoles = new UserRoles(user, optRole.get());
			userRoles = userRolesRepository.save(userRoles);
			List<UserRoles> roles = user.getUserRoles();
			roles.add(userRoles);
			user.setUserRoles(roles);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
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
	
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
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