package com.example.demo.security;

import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
	
	private final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private static final String USER_ROLE_NAME = "ROLE_USER";
	
	public void registerUser(User user) {
		
		Optional<Role> optRole = roleRepository.findByName(USER_ROLE_NAME);

		if (optRole.isEmpty()) {
			log.info("Role {} not found in database.", USER_ROLE_NAME);
			throw new RuntimeException("Role not found.");
		} else {
			if (userRepository.findByUsername(user.getUsername()).isPresent()) throw new EntityExistsException("User already exists.");
			user.addRole(optRole.get());
			userRepository.save(user);
		}		
	}
	
	public void updateLastLoginDateTime(String username) {
		Optional<User> optUser = userRepository.findByUsername(username);
		if (optUser.isPresent()) {
			User user = optUser.get();
			user.updateLastLoginDateTime();
			userRepository.save(user);
		}		
	}
	
}