package com.example.demo.security;

import static java.util.Collections.emptyList;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationService {
  static final long EXPIRY_MINS = 60L;
  static final String SIGNINGKEY = "VeryLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongSecretKey";
  static final String PREFIX = "Bearer";

  static public void addToken(HttpServletResponse res, String username) {
	Date expirationDateTime = Date.from(LocalDateTime.now().plusMinutes(EXPIRY_MINS).atZone(ZoneId.systemDefault()).toInstant());
    String JwtToken = Jwts.builder().setSubject(username)
        .setExpiration(expirationDateTime)
        .signWith(SignatureAlgorithm.HS512, SIGNINGKEY)
        .compact();
    res.addHeader("Authorization", PREFIX + " " + JwtToken);
    res.addHeader("Access-Control-Expose-Headers", "Authorization");
  }

  static public Authentication getAuthentication(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null) {
      String user = Jwts.parser()
          .setSigningKey(SIGNINGKEY)
          .parseClaimsJws(token.replace(PREFIX, ""))
          .getBody()
          .getSubject();

      if (user != null)
        return new UsernamePasswordAuthenticationToken(user, null, emptyList());
    }
    return null;
  }
}
