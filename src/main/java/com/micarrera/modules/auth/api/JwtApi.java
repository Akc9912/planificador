package com.micarrera.modules.auth.api;

import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import com.nimbusds.jwt.JWTClaimsSet;

public interface JwtApi {
    JWTClaimsSet parseClaims(String token);
    String generateInternalToken(String subject, Object userId, String role);
    String extractEmail(String token);
    String extractUsername(String token);
    UUID extractUserId(String token);
    String extractRole(String token);
    boolean isTokenValid(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean validateTokenForEmail(String token, String email);
    UsernamePasswordAuthenticationToken getAuthentication(String token, UserDetails userDetails);
}
