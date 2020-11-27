package com.example.demo.security;

import static java.util.Collections.emptyList;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class AuthenticationService {
  static final long EXPIRY_MINS = 60L;
  // We need a signing key, so we'll create one just for this example. Usually
  // the key would be read from your application configuration instead.
  static final Key SIGNINGKEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
  static final String PREFIX = "Bearer";

  static public void addToken(HttpServletResponse res, String username) {
	Date expirationDateTime = Date.from(
			LocalDateTime.now().plusMinutes(EXPIRY_MINS).atZone(ZoneId.systemDefault()).toInstant()
			);
    String JwtToken = Jwts.builder().setSubject(username)
        .setExpiration(expirationDateTime)
        .signWith(SIGNINGKEY)
        .compact();
    res.addHeader("Authorization", PREFIX + " " + JwtToken);
    res.addHeader("Access-Control-Expose-Headers", "Authorization");
  }

  static public Authentication getAuthentication(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null) {
      String username = Jwts.parserBuilder()
          .setSigningKey(SIGNINGKEY)
          .build()
          .parseClaimsJws(token.replace(PREFIX, ""))
          .getBody()
          .getSubject();

      if (username != null)
        return new UsernamePasswordAuthenticationToken(username, null, emptyList());
    }
    return null;
  }
}
