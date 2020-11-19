package com.example.demo.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@ConditionalOnClass({GoogleClientProperties.class})
@EnableConfigurationProperties(GoogleClientProperties.class)
public class GoogleClientConfiguration {

    private final GoogleClientProperties googleClientProperties;

    @Autowired
    public GoogleClientConfiguration(GoogleClientProperties googleClientProperties) {
        this.googleClientProperties = googleClientProperties;
    }

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(JacksonFactory jacksonFactory, HttpTransport httpTransport) {
        return new GoogleIdTokenVerifier.Builder(httpTransport, jacksonFactory)
                .setIssuer(googleClientProperties.getIssuer())
                .setAudience(Arrays.asList(googleClientProperties.getClients()))
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