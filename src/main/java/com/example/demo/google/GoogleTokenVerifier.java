package com.example.demo.google;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

@Component
public class GoogleTokenVerifier {

    private GoogleIdTokenVerifier googleIdTokenVerifier;

    public GoogleTokenVerifier(GoogleIdTokenVerifier googleIdTokenVerifier) {
        this.googleIdTokenVerifier = googleIdTokenVerifier;
    }

    public GoogleIdToken verify(String idToken) throws GeneralSecurityException, IOException {
        return googleIdTokenVerifier.verify(idToken);
    }
}