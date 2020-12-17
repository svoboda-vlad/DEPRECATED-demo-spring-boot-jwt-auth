package com.example.demo.google;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleTokenVerifier {

	private final Logger log = LoggerFactory.getLogger(GoogleTokenVerifier.class);

	private final GoogleIdTokenVerifier googleIdTokenVerifier;

	public GoogleIdToken verify(String idToken) {
		try {
			GoogleIdToken token = googleIdTokenVerifier.verify(idToken);
			if (token == null) log.info("ID token verification by Google failed. ID token: {}.", idToken);
			return token;
		} catch (GeneralSecurityException e) {
			log.info("ID token verification by Google failed: {}. ID token: {}.", e.getMessage(), idToken);
		} catch (IOException e) {
			log.info("ID token verification by Google failed: {}. ID token: {}.", e.getMessage(), idToken);
		} catch (IllegalArgumentException e) {
			log.info("ID token verification by Google failed: {}. ID token: {}.", e.getMessage(), idToken);
		}
		return null;
	}
}