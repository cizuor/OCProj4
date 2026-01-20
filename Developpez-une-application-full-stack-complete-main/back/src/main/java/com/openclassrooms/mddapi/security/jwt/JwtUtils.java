package com.openclassrooms.mddapi.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;


import javax.crypto.SecretKey; 

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.openclassrooms.mddapi.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; 
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtils {
	
	@Value("${oc.app.jwtSecret}")
	private String jwtSecret;

	@Value("${oc.app.jwtExpirationMs}")
	private int jwtExpirationMs;
	
	private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey()) 
                .compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token) 
                .getPayload() 
                .getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
			return true;
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}
}
