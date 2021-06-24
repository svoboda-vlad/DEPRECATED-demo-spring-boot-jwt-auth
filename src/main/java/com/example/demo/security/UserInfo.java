package com.example.demo.security;

import java.time.LocalDateTime;

import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class UserInfo {

	private String username;
		
    @Size(min = 1, max = 255)
    @NonNull
	private String givenName;
    
    @Size(min = 1, max = 255)
    @NonNull
	private String familyName;
    
    private LocalDateTime lastLoginDateTime;
    private LocalDateTime previousLoginDateTime;    

	public User toUser(User user) {
		user.setFamilyName(familyName);
		user.setGivenName(givenName);
		return user;
	}    
    
}