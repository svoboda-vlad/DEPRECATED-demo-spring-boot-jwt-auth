package com.example.demo.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().cors().and().authorizeRequests()
		.antMatchers(HttpMethod.POST, "/login").permitAll()
		.anyRequest().authenticated()
		.and()
		// Filter for the api/login requests
		.addFilterBefore(new LoginFilter("/login", authenticationManager()), 
		UsernamePasswordAuthenticationFilter.class)
	    // Filter for other requests to check JWT in header
	    .addFilterBefore(new AuthenticationFilter(),
	    UsernamePasswordAuthenticationFilter.class);
	}	
	
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user =
			 User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(user);
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
}
