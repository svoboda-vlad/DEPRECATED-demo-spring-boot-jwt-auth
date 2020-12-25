package com.example.demo.security;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.demo.google.GoogleTokenVerifier;
import com.example.demo.google.IdToken;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

public class LoginGoogleFilter extends AbstractAuthenticationProcessingFilter {

	private final Logger log = LoggerFactory.getLogger(LoginGoogleFilter.class);
	
	private final GoogleTokenVerifier googleTokenVerifier;
	
	private final UserRepository userRepo;

	public LoginGoogleFilter(String url, AuthenticationManager authManager, GoogleTokenVerifier googleTokenVerifier, UserRepository userRepo) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
		this.googleTokenVerifier = googleTokenVerifier;
		this.userRepo = userRepo;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
			IdToken idToken = resolveIdToken(req);
			if (idToken == null) throw new AuthenticationServiceException("ID token parsing from request body failed.");
			
			GoogleIdToken googleIdToken = googleTokenVerifier.verify(idToken.getIdToken());
			if (googleIdToken == null) throw new AuthenticationServiceException("ID token verification by Google failed.");			

			GoogleIdToken.Payload payload = googleIdToken.getPayload();
			String sub = payload.get("sub").toString();
			log.info("Authenticating user {}", sub);
			Authentication auth = this.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(sub, sub));
			this.updateLastLoginDateTime(auth);
			log.info("User: {} authenticated: {}", sub, auth.isAuthenticated());
			return auth;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		AuthenticationService.addToken(res, auth.getName());
	}
	
	private IdToken resolveIdToken(HttpServletRequest request)  {		
		try {
			return new ObjectMapper().readValue(request.getInputStream(), IdToken.class);
		} catch (JsonParseException e) {
			log.info("ID token parsing from request body failed: {}.", e.getMessage());
		} catch (JsonMappingException e) {
			log.info("ID token parsing from request body failed: {}.", e.getMessage());
		} catch (IOException e) {
			log.info("ID token parsing from request body failed: {}.", e.getMessage());
		}
		return null;
	}
	
	private void updateLastLoginDateTime(Authentication auth) {
		User user = userRepo.findByUsername(auth.getName());
		user.setLastLoginDateTime(LocalDateTime.now());
		userRepo.save(user);
	}
	
}
