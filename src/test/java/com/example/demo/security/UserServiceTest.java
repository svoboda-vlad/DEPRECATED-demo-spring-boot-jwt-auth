package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.security.User.LoginProvider;

@SpringBootTest
//@WithMockUser - not needed
public class UserServiceTest {
	
	private static final String USER_ROLE_NAME = "ROLE_USER";
	
	@MockBean
	private RoleRepository roleRepository;
	
	@MockBean
	private UserRepository userRepository;
		
	@Autowired
	private UserService userService;
	
	@Test
	void testRegisterUserNewUser() {		
		Role role = new Role(USER_ROLE_NAME);
		User user = new User("user", StringUtils.repeat("A", 60), LoginProvider.INTERNAL,"User","User");
		user.addRole(role);
		
		given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role));
		given(userRepository.findByUsername("user")).willReturn(Optional.empty());
		given(userRepository.save(user)).willReturn(user);
		
		assertThat(userService.registerUser(user)).isEqualTo(user);
	}
	
	@Test
	void testRegisterUserAlreadyExistsException() {		
		Role role = new Role(USER_ROLE_NAME);
		User user = new User("user", StringUtils.repeat("A", 60), LoginProvider.INTERNAL,"User","User");
		
		given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role));
		given(userRepository.findByUsername("user")).willReturn(Optional.of(user));
		
	    assertThatExceptionOfType(EntityExistsException.class)
        .isThrownBy(() -> { userService.registerUser(user); });		
		
	}
	
	@Test
	void testRegisterUserDefaultRoleNotFound() {		
		User user = new User("user", StringUtils.repeat("A", 60), LoginProvider.INTERNAL,"User","User");
		
		given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.empty());
		
	    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> { userService.registerUser(user); });		
	}	
	
	@Test
	void testUpdateLastLoginDateTime() {
		String username = "user";
		User user = new User("user", StringUtils.repeat("A", 60), LoginProvider.INTERNAL,"User","User");		

		given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
		given(userRepository.save(user)).willReturn(user);
		
		assertThat(userService.updateLastLoginDateTime(username)).isEqualTo(user);
	}

	@Test
	void testUpdateUserOkUserExists() {
		String username = "user";
		User user = new User("user", StringUtils.repeat("A", 60), LoginProvider.INTERNAL,"User","User");
		UserInfo userInfo = new UserInfo("user", "User", "User");

		given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
		given(userRepository.save(userInfo.toUser(user))).willReturn(user);
		assertThat(userService.updateUser(userInfo)).isEqualTo(user);
	}
	
}
