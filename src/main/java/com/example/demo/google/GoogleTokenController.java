package com.example.demo.google;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

@RestController
public class GoogleTokenController {

    private final GoogleTokenVerifier googleTokenVerifier;

    public GoogleTokenController(GoogleTokenVerifier googleTokenVerifier) {
        this.googleTokenVerifier = googleTokenVerifier;
    }

    @PostMapping("/verify")
    public User verifyToken(@RequestBody IdToken idToken) throws GeneralSecurityException, IOException {
        GoogleIdToken googleIdToken = googleTokenVerifier.verify(idToken.getIdToken());
        if (googleIdToken == null) {
            throw new RuntimeException("Unauthenticated User by google");
        }
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        return new User(payload.get("given_name").toString(), payload.get("family_name").toString());
    }
}