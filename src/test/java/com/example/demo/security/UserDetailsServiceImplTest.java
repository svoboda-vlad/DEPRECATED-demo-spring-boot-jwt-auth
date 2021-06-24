package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.security.User.LoginProvider;

@SpringBootTest
class UserDetailsServiceImplTest {

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserDetailsService userDetailsService;

	@MockBean
	private UserRepository userRepository;

	@Test
	void testLoadUserByUsernameOk() {
		User user = new User("user", encoder.encode("password"),LoginProvider.INTERNAL,"User","User");

		given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

		assertThat(userDetailsService.loadUserByUsername(user.getUsername())).isEqualTo(user);
	}

	@Test
	void testLoadUserByUsernameThrowsException() {
		User user = new User("user", encoder.encode("password"),LoginProvider.INTERNAL,"User","User");

		given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.empty());

	    assertThatExceptionOfType(UsernameNotFoundException.class)
        .isThrownBy(() -> { userDetailsService.loadUserByUsername(user.getUsername()); });
	}

}
