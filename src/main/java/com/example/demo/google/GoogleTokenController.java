package com.example.demo.google;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GoogleTokenController {

    private final GoogleTokenVerifier googleTokenVerifier;

    @PostMapping("/verify")
    public ResponseEntity<User> verifyToken(@RequestBody IdToken idToken) {
        GoogleIdToken googleIdToken;
		try {
			googleIdToken = googleTokenVerifier.verify(idToken.getIdToken());
	        if (googleIdToken == null) {
	            throw new RuntimeException("Unauthenticated user by Google");
	        }
	        GoogleIdToken.Payload payload = googleIdToken.getPayload();
	        User user = new User(payload.get("given_name").toString(), payload.get("family_name").toString(), payload.get("sub").toString());
	        return new ResponseEntity<>(user, HttpStatus.OK);			
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}		
    }
}