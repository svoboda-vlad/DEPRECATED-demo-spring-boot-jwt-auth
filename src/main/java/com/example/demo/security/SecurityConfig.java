package com.example.demo.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.google.GoogleTokenVerifier;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private GoogleTokenVerifier googleTokenVerifier;
	
	@Autowired
	private AuthenticationService authenticationService;	

	@Autowired
	private UserDetailsService userDetailsService;	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().cors().and().authorizeRequests()
		.antMatchers(HttpMethod.POST, "/login-google").permitAll()
		.antMatchers("/h2-console/**", "/test").permitAll()		
		.anyRequest().authenticated()
		.and()
		// Filter for the api/login-google requests
		.addFilterBefore(new LoginGoogleFilter("/login-google", authenticationManager(), googleTokenVerifier()), 
		UsernamePasswordAuthenticationFilter.class)
	    // Filter for other requests to check JWT in header
	    .addFilterBefore(new AuthenticationFilter(authenticationService()),
		UsernamePasswordAuthenticationFilter.class)
		.headers().frameOptions().disable();
	}	
		
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		return userDetailsService;
	}
		
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
	      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		  CorsConfiguration config = new CorsConfiguration();
		  config.setAllowedOrigins(Arrays.asList("*"));
		  config.setAllowedMethods(Arrays.asList("*"));
		  config.setAllowedHeaders(Arrays.asList("*"));
		  config.setAllowCredentials(true);
		  config.applyPermitDefaultValues();
	      
	      source.registerCorsConfiguration("/**", config);
	      return source;
	}
	
	public GoogleTokenVerifier googleTokenVerifier() {
		return googleTokenVerifier;
	}
	
	public AuthenticationService authenticationService() {
		return authenticationService;
	}	
	
}
