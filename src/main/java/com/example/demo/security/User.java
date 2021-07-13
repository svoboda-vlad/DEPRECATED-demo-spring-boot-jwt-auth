package com.example.demo.security;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "user", schema = "public") // needed for PostgreSQL
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    @NotNull
    @Size(min = 1, max = 255)
    @NonNull
	private String username;

    @NotNull
    @Size(min = 60, max = 60)
    @NonNull
    private String password;
    
    @NotNull
    @NonNull
    @Enumerated(EnumType.STRING)
    private LoginProvider loginProvider;
    
    private LocalDateTime lastLoginDateTime;
    private LocalDateTime previousLoginDateTime;
    
    @NotNull
    @Size(min = 1, max = 255)
    @NonNull
	private String givenName;

    @NotNull
    @Size(min = 1, max = 255)
    @NonNull
	private String familyName;
    
    // CascadeType.ALL - enable removing the relation (user_roles.user_id)
    // orphanRemoval - enable removing the related entity (user_roles)
    // fetch - changed to eager
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<UserRoles> userRoles = new ArrayList<UserRoles>();
    
    
	public void addRole(Role role) {
		UserRoles roles = new UserRoles(this, role);
		userRoles.add(roles);
	}
	
	public void removeRole(Role role) {		
		UserRoles roles = new UserRoles(this, role);
		userRoles.remove(roles);
		roles.setUser(null);
		roles.setRole(null);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        for (UserRoles role : this.userRoles) {
        	roles.add(new SimpleGrantedAuthority(role.getRole().getName()));
        }
        return roles;
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
	
	public void updateLastLoginDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		if (this.lastLoginDateTime == null) {
			this.previousLoginDateTime = currentDateTime;
		} else {
			this.previousLoginDateTime = this.lastLoginDateTime;
		}
		this.lastLoginDateTime = currentDateTime;
	}
	
	public enum LoginProvider {	
		INTERNAL,
		GOOGLE
	}
	
	public UserInfo toUserInfo() {
		UserInfo userInfo = new UserInfo(this.getGivenName(), this.getFamilyName());
		userInfo.setUsername(username);
		userInfo.setLastLoginDateTime(lastLoginDateTime);
		userInfo.setPreviousLoginDateTime(previousLoginDateTime);
		userInfo.setUserRoles(userRoles);
		return userInfo;
	}
	
	public void addUserRoles(UserRoles userRoles) {
		this.userRoles.add(userRoles);
		// @NotNull applied on user
		// userRoles.setUser(this);
	}	
	
}