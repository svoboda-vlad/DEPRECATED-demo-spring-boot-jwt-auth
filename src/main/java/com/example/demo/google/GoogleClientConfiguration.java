package com.example.demo.google;

import java.util.Arrays;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableConfigurationProperties(GoogleClientProperties.class)
@RequiredArgsConstructor
public class GoogleClientConfiguration {

    private final GoogleClientProperties googleClientProperties;

    @Bean
    public GsonFactory gsonFactory() {
        return new GsonFactory();
    }

    @Bean
    public HttpTransport httpTransport() {
        return new NetHttpTransport();
    }
    
    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
    	return new GoogleIdTokenVerifier.Builder(httpTransport(), gsonFactory())
    			.setAudience(Arrays.asList(googleClientProperties.getClientIds())).build();
    }    

}