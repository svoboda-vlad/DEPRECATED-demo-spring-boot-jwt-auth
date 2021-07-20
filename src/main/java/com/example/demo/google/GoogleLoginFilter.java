package com.example.demo.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import javax.persistence.EntityExistsException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.demo.security.AuthenticationService;
import com.example.demo.security.User;
import com.example.demo.security.UserRegister;
import com.example.demo.security.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

public class GoogleLoginFilter extends AbstractAuthenticationProcessingFilter {

	private final Logger log = LoggerFactory.getLogger(GoogleLoginFilter.class);

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private GoogleIdTokenVerifier googleIdTokenVerifier;
	
	@Autowired
	private UserService userService;
	
	public GoogleLoginFilter(AuthenticationManager authManager) {
		super(new AntPathRequestMatcher("/google-login", "POST"));
		this.setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException {

		GoogleIdTokenEntity tokenEntity = resolveGoogleIdTokenEntity(req);
		if (tokenEntity == null)
			throw new BadCredentialsException("");
		String username = "";

		try {
			GoogleIdToken idToken = googleIdTokenVerifier.verify(tokenEntity.getIdToken());
			if (idToken != null) {
				Payload payload = idToken.getPayload();
				username = payload.getSubject();

				String familyName = (String) payload.get("family_name");
				String givenName = (String) payload.get("given_name");
				UserRegister userRegister = new UserRegister(username, username, givenName, familyName);
				User user = userRegister.toUserGoogle(encoder);
				try {
					userService.registerUser(user);	
				} catch (EntityExistsException e){
					log.info(e.toString());
				}
			}
		} catch (GeneralSecurityException | IOException e) {
			log.info("Google ID token verification failed");
			throw new BadCredentialsException("");
		}
		return getAuthenticationManager()
				.authenticate(new UsernamePasswordAuthenticationToken(username, username, Collections.emptyList()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		AuthenticationService.addToken(res, auth.getName());
		
		userService.updateLastLoginDateTime(auth.getName());
	}

	private GoogleIdTokenEntity resolveGoogleIdTokenEntity(HttpServletRequest request) {
		try {
			return new ObjectMapper().readValue(request.getInputStream(), GoogleIdTokenEntity.class);
		} catch (Exception e) {
			log.info("ID token parsing from request body failed: {}.", e.getMessage());
		}
		return null;
	}

}