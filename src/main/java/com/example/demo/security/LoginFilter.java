package com.example.demo.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
	
	private final Logger log = LoggerFactory.getLogger(LoginFilter.class);

	@Autowired
	private UserRepository userRepository;

	public LoginFilter(AuthenticationManager authManager) {
		super(new AntPathRequestMatcher("/login", "POST"));
		this.setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException {
		User user = resolveUser(req);
		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),
				user.getPassword(), Collections.emptyList()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		AuthenticationService.addToken(res, auth.getName());
		User user = userRepository.findByUsername(auth.getName());
		user.updateLastLoginDateTime();
		userRepository.save(user);
	}
	
	private User resolveUser(HttpServletRequest request)  {		
		try {
			return new ObjectMapper().readValue(request.getInputStream(), User.class);
		} catch (Exception e) {
			log.info("Username and password parsing from request body failed: {}.", e.getMessage());
		}
		return new User();
	}	
}