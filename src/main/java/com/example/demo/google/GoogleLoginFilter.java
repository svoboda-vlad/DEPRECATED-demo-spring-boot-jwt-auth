package com.example.demo.google;

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

import com.example.demo.security.AuthenticationService;
import com.example.demo.security.User;
import com.example.demo.security.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

public class GoogleLoginFilter extends AbstractAuthenticationProcessingFilter {

	private final Logger log = LoggerFactory.getLogger(GoogleLoginFilter.class);	
	
	@Autowired
	private GoogleIdTokenVerifier googleIdTokenVerifier;
	
	@Autowired
	private UserRepository userRepository;

	public GoogleLoginFilter(AuthenticationManager authManager) {
		super(new AntPathRequestMatcher("/google-login", "POST"));
		this.setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException {

		GoogleIdTokenEntity tokenEntity = resolveGoogleIdTokenEntity(req);		
		String username = "";
		try {
			GoogleIdToken idToken = googleIdTokenVerifier.verify(tokenEntity.getIdToken());
			if (idToken != null) {
				Payload payload = idToken.getPayload();
				username = payload.getSubject();
			}
		} catch (Exception e) {
			log.info("Google ID token verification failed");
		}
		return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(username, "", Collections.emptyList()));		
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		AuthenticationService.addToken(res, auth.getName());
		User user = userRepository.findByUsername(auth.getName());
		user.updateLastLoginDateTime();
		userRepository.save(user);
	}
	
	private GoogleIdTokenEntity resolveGoogleIdTokenEntity(HttpServletRequest request)  {		
		try {
			return new ObjectMapper().readValue(request.getInputStream(), GoogleIdTokenEntity.class);
		} catch (Exception e) {
			log.info("ID token parsing from request body failed: {}.", e.getMessage());
		}
		return new GoogleIdTokenEntity();
	}	
	
}