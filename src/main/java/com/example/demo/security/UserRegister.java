package com.example.demo.security;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.security.User.LoginProvider;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class UserRegister {

	@NotNull
	@Size(min = 1, max = 255)
	@NonNull
	private String username;
	
	@NotNull
	@Size(min = 4, max = 100)
	@NonNull
	private String password;
	
    @NotNull
    @Size(min = 1, max = 255)
    @NonNull
	private String givenName;
    
    @NotNull
    @Size(min = 1, max = 255)
    @NonNull
	private String familyName;

	public User toUserInternal(PasswordEncoder passwordEncoder) {
		User user = new User(username, passwordEncoder.encode(password), LoginProvider.INTERNAL);
		user.setFamilyName(familyName);
		user.setGivenName(givenName);
		return user;
	}
	
	public User toUserGoogle(PasswordEncoder passwordEncoder) {
		User user = new User(username, passwordEncoder.encode(password), LoginProvider.GOOGLE);
		user.setFamilyName(familyName);
		user.setGivenName(givenName);
		return user;		
	}

}
