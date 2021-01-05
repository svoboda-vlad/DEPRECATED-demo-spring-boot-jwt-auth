package com.example.demo.security;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
@RequiredArgsConstructor
public class User implements UserDetails {
		
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

	/*
	 * "sub": "108564931079495851483", "email": "svoboda.vladimir1@gmail.com",
	 * "given_name": "Vladimir", "family_name": "Svoboda", "locale": "cs",
	 */    
    
    @NotNull
    @Size(min = 1, max = 255)
    // the account's ID from the sub claim of the ID token
    private final String username;

    @NotNull
    @Size(min = 1, max = 255)
    private final String email;
    
    @NotNull
    @Size(min = 1, max = 255)
    private final String givenName;

    @NotNull
    @Size(min = 1, max = 255)
    private final String familyName;
    
    @NotNull
    @Size(min = 1, max = 255)
    private final String locale;
    
    private LocalDateTime lastLoginDateTime;
        
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    @Override
    public boolean isAccountNonLocked() {
      return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

	@Override
	public String getPassword() {
		return username;
	}
}
