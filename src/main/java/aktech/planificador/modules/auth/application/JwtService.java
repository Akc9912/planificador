
package aktech.planificador.modules.auth.application;

import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

@Service("moduleAuthJwtService")
public class JwtService {
    private static final String ENV_JWKS_URL = "SUPABASE_JWKS_URL";

    private static String getJwksUrlFromEnv() {
        String url = System.getenv(ENV_JWKS_URL);
        if (url == null || url.isBlank()) {
            throw new IllegalStateException(
                    "Falta la variable de entorno SUPABASE_JWKS_URL para la JWKS endpoint de Supabase");
        }
        return url;
    }

    private final DefaultJWTProcessor<SecurityContext> jwtProcessor;

    public JwtService() throws Exception {
        URL jwksUrl = new URL(getJwksUrlFromEnv());
        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(jwksUrl);
        jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(
                new JWSAlgorithmFamilyJWSKeySelector<SecurityContext>(JWSAlgorithm.Family.EC, keySource));
    }

    public JWTClaimsSet parseClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            SecurityContext ctx = null;
            return jwtProcessor.process(signedJWT, ctx);
        } catch (Exception e) {
            throw new RuntimeException("Token inválido o no verificable", e);
        }
    }

    // No se usa generación de tokens internos con ES256/JWKS
    public String generateInternalToken(String subject, Object userId, String role) {
        throw new UnsupportedOperationException("Token generation not supported with ES256/JWKS");
    }


    public String extractEmail(String token) {
        JWTClaimsSet claims = parseClaims(token);
        String subject = claims.getSubject();
        if (subject != null && subject.contains("@")) {
            return subject;
        }
        Object emailClaim = claims.getClaim("email");
        return emailClaim != null ? emailClaim.toString() : null;
    }


    public String extractUsername(String token) {
        return extractEmail(token);
    }


    public UUID extractUserId(String token) {
        JWTClaimsSet claims = parseClaims(token);
        UUID fromSubject = parseUuid(claims.getSubject());
        if (fromSubject != null) {
            return fromSubject;
        }
        UUID fromUserIdClaim = parseUuidObject(claims.getClaim("user_id"));
        if (fromUserIdClaim != null) {
            return fromUserIdClaim;
        }
        return parseUuidObject(claims.getClaim("id"));
    }


    public String extractRole(String token) {
        JWTClaimsSet claims = parseClaims(token);
        Object legacyRole = claims.getClaim("rol");
        if (legacyRole != null && !legacyRole.toString().isBlank()) {
            return legacyRole.toString();
        }
        Object role = claims.getClaim("role");
        if (role != null && !role.toString().isBlank()) {
            return role.toString();
        }
        Object appMetadata = claims.getClaim("app_metadata");
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
            parseClaims(token); // throws if invalid
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
        Date expiration = parseClaims(token).getExpirationTime();
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
