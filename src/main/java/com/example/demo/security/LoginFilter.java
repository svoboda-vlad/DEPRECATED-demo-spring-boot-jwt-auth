package com.example.demo.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
	
	private final UserRepository userRepository;

  public LoginFilter(String url, AuthenticationManager authManager, UserRepository userRepository) {
    super(new AntPathRequestMatcher(url));
    setAuthenticationManager(authManager);
    this.userRepository = userRepository;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException {
        User user = new ObjectMapper()
        .readValue(req.getInputStream(), User.class);
        return getAuthenticationManager().authenticate(
          new UsernamePasswordAuthenticationToken(
            user.getUsername(),
            user.getPassword(),
            Collections.emptyList()
            )
        );
  }

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		AuthenticationService.addToken(res, auth.getName());
		User user = userRepository.findByUsername(auth.getName());
		user.updateLastLoginDateTime();
		userRepository.save(user);
	}
}