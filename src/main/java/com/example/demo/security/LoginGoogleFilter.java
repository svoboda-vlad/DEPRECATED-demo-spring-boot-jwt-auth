package com.example.demo.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	private final GoogleTokenVerifier googleTokenVerifier;

	public LoginGoogleFilter(String url, AuthenticationManager authManager, GoogleTokenVerifier googleTokenVerifier) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
		this.googleTokenVerifier = googleTokenVerifier;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {
			IdToken idToken = resolveIdToken(req);
			GoogleIdToken googleIdToken = googleTokenVerifier.verify(idToken.getIdToken());
			if (googleIdToken == null) {
				throw new AuthenticationServiceException("Unauthenticated user by Google");
			}
			GoogleIdToken.Payload payload = googleIdToken.getPayload();
			return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
					payload.get("sub").toString(), "", Collections.emptyList()));			
		} catch (Exception e) {
			throw new AuthenticationServiceException(e.toString());
		}
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
			throw new AuthenticationServiceException(e.toString());
		} catch (JsonMappingException e) {
			throw new AuthenticationServiceException(e.toString());
		} catch (IOException e) {
			throw new AuthenticationServiceException(e.toString());
		}
	}
}
