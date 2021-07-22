package com.example.demo;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.security.User;
import com.example.demo.security.UserRegister;
import com.example.demo.security.UserService;

@Component
public class StartupCommandLineRunner implements CommandLineRunner {

	@Autowired
	private AdminUserBean adminUser;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Override
	public void run(String... args) throws Exception {
		saveAdminUser();
	}

	private void saveAdminUser() {
		if (adminUser.getUsername() != null && adminUser.getPassword() != null) {
			UserRegister userRegister = new UserRegister(adminUser.getUsername(), adminUser.getPassword(), "Administator", "Administrator");
			User user = userRegister.toUserInternal(encoder);
			try {
				userService.registerAdminUser(user);
			} catch (EntityExistsException e) {
				
			}
		}		
	}
}