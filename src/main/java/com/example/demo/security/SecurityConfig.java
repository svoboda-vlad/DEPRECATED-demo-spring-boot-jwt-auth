package com.example.demo.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.google.GoogleLoginFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthenticationFilter authenticationFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().cors().and().authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers(HttpMethod.POST, "/login", "/register").permitAll()
				.antMatchers("/h2-console/**", "/current-user").permitAll()
				.antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll().anyRequest()
				.hasRole("USER")
				.and()
				// Filter for login requests
				.addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class)
				// Filter for login requests
				.addFilterBefore(googleLoginFilter(), UsernamePasswordAuthenticationFilter.class)
				// Filter for other requests to check JWT in header
				.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class).headers()
				.frameOptions().disable().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(Arrays.asList("*"));
		config.setAllowedMethods(Arrays.asList("*"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowCredentials(true);
		config.applyPermitDefaultValues();

		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public LoginFilter loginFilter() throws Exception {
		return new LoginFilter(authenticationManager());
	}

	@Bean
	public GoogleLoginFilter googleLoginFilter() throws Exception {
		return new GoogleLoginFilter(authenticationManager());
	}
	
}
