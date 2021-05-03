package com.example.demo.security;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RegistrationController {
	
	private final String REGISTRATION_URL = "/register";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
    
    
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping(REGISTRATION_URL)
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationUser registrationUser) {
    	userRepository.save(registrationUser.toUser(passwordEncoder));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }    
	
}
