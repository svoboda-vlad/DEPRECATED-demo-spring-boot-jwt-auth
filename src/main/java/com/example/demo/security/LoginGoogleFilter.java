package com.example.demo.security;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.demo.google.GoogleTokenVerifier;
import com.example.demo.google.IdToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

public class LoginGoogleFilter extends AbstractAuthenticationProcessingFilter {

	@Autowired
	private GoogleTokenVerifier googleTokenVerifier;

	public LoginGoogleFilter(String url, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException {
		IdToken idToken = new ObjectMapper().readValue(req.getInputStream(), IdToken.class);

		try {
			GoogleIdToken googleIdToken = googleTokenVerifier.verify(idToken.getIdToken());
			if (googleIdToken == null) {
				throw new RuntimeException("Unauthenticated User by google");
			}
			GoogleIdToken.Payload payload = googleIdToken.getPayload();

			return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
					payload.get("sub").toString(), "", Collections.emptyList()));
		} catch (GeneralSecurityException e) {
			throw new RuntimeException("Unauthenticated User by google");
		} catch (IOException e) {
			throw new RuntimeException("Unauthenticated User by google");
		}

	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		AuthenticationService.addToken(res, auth.getName());
	}
}
