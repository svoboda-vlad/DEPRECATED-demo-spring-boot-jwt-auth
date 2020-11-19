package com.example.demo.google;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@RestController
public class GoogleTokenController {

    // private GoogleTokenVerifier googleTokenVerifier;

    /*@Autowired
    public GoogleTokenController(GoogleTokenVerifier googleTokenVerifier) {
        this.googleTokenVerifier = googleTokenVerifier;
    }*/

    @PostMapping("/verify")
    public @ResponseBody User verifyToken(@RequestBody IdToken idToken) throws GeneralSecurityException, IOException {
        // GoogleIdToken googleIdToken = googleTokenVerifier.verify(idToken.getIdToken());
    	GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
    			.setAudience(Collections.singletonList("733460469950-84s81fm32dvqku5js9rvlf6llqekr6l4.apps.googleusercontent.com"))
    		    .build();
    	GoogleIdToken googleIdToken = verifier.verify(idToken.getIdToken());
        if (isNull(googleIdToken)) {
            throw new RuntimeException("Unauthenticated User by google");
        }
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        return new User((String) payload.get("given_name"), (String) payload.get("family_name"));
    }
}