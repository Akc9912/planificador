package aktech.planificador.Service.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import aktech.planificador.Model.core.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private long jwtExpirationMs = 1000 * 60 * 60 * 10; // 10 horas

    private SecretKey getSigningKey() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 caracteres");
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Usuario usuario) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(usuario.getEmail())
        .claim("id", usuario.getId())
        .issuedAt(new Date(now))
        .expiration(new Date(now + jwtExpirationMs))
        .signWith(getSigningKey())
        .compact();
    }


    // Extrae el email del token (antes username)
    public String extractUsername(String token) {
        return extractEmail(token);
    }

    public String extractEmail(String token) {
        return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
    }


    // Valida el token con UserDetails usando email
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        // userDetails.getUsername() debe devolver el email
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    // Valida el token con la entidad Usuario
    public boolean validateToken(String token, Usuario usuario) {
        final String email = extractEmail(token);
        return (email.equals(usuario.getEmail()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
        .verifyWith(getSigningKey())
        .build().parseSignedClaims(token)
        .getPayload()
        .getExpiration();
        return expiration.before(new Date());
    }


    // Genera el objeto de autenticación usando email
    public UsernamePasswordAuthenticationToken getAuthentication(String token, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
