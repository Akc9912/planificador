package aktech.planificador.modules.auth.application;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service("moduleAuthJwtService")
public class JwtService {
    private final String jwtSecret;
    private final long expirationMs;

    public JwtService(
            @Value("${supabase.jwt.secret:${jwt.secret:default-key-change-me-default-key-change-me}}") String jwtSecret,
            @Value("${jwt.expiration.ms:86400000}") long expirationMs) {
        this.jwtSecret = jwtSecret;
        this.expirationMs = expirationMs;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateInternalToken(String subject, Object userId, String role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("id", userId)
                .claim("rol", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        Claims claims = parseClaims(token);
        String subject = claims.getSubject();

        if (subject != null && subject.contains("@")) {
            return subject;
        }

        Object emailClaim = claims.get("email");
        return emailClaim != null ? emailClaim.toString() : null;
    }

    public String extractUsername(String token) {
        return extractEmail(token);
    }

    public UUID extractUserId(String token) {
        Claims claims = parseClaims(token);

        UUID fromSubject = parseUuid(claims.getSubject());
        if (fromSubject != null) {
            return fromSubject;
        }

        UUID fromUserIdClaim = parseUuidObject(claims.get("user_id"));
        if (fromUserIdClaim != null) {
            return fromUserIdClaim;
        }

        return parseUuidObject(claims.get("id"));
    }

    public String extractRole(String token) {
        Claims claims = parseClaims(token);

        Object legacyRole = claims.get("rol");
        if (legacyRole != null && !legacyRole.toString().isBlank()) {
            return legacyRole.toString();
        }

        Object role = claims.get("role");
        if (role != null && !role.toString().isBlank()) {
            return role.toString();
        }

        Object appMetadata = claims.get("app_metadata");
        if (appMetadata instanceof Map<?, ?> map) {
            Object nestedRole = map.get("role");
            if (nestedRole != null && !nestedRole.toString().isBlank()) {
                return nestedRole.toString();
            }
        }

        return "USER";
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        if (username == null || userDetails == null) {
            return false;
        }
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateTokenForEmail(String token, String email) {
        final String tokenEmail = extractEmail(token);
        if (tokenEmail == null || email == null || email.isBlank()) {
            return false;
        }
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private UUID parseUuidObject(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof UUID uuid) {
            return uuid;
        }

        return parseUuid(value.toString());
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
