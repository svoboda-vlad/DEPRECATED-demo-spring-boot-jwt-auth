package com.example.demo.google;

import java.util.Arrays;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@Configuration
@EnableConfigurationProperties(GoogleClientProperties.class)
public class GoogleClientConfiguration {

    private final GoogleClientProperties googleClientProperties;

    public GoogleClientConfiguration(GoogleClientProperties googleClientProperties) {
        this.googleClientProperties = googleClientProperties;
    }

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(JacksonFactory jacksonFactory, HttpTransport httpTransport) {
        return new GoogleIdTokenVerifier.Builder(httpTransport, jacksonFactory)
                .setAudience(Arrays.asList(googleClientProperties.getClientIds()))
                .build();
    }

    @Bean
    public JacksonFactory jacksonFactory() {
        return new JacksonFactory();
    }

    @Bean
    public HttpTransport httpTransport() {
        return new NetHttpTransport();
    }

}