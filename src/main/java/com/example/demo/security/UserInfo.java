package com.example.demo.security;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
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
		
	@NotNull
    @Size(min = 1, max = 255)
    @NonNull
	private String givenName;
    
	@NotNull
    @Size(min = 1, max = 255)
    @NonNull
	private String familyName;
    
    private LocalDateTime lastLoginDateTime;
    private LocalDateTime previousLoginDateTime;
	private List<UserRoles> userRoles = new ArrayList<UserRoles>();
    
	public User toUser(User user) {
		user.setFamilyName(familyName);
		user.setGivenName(givenName);
		return user;
	}
    
}