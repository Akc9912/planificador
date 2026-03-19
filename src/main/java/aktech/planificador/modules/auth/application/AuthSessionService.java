package aktech.planificador.modules.auth.application;

import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;

import aktech.planificador.modules.auth.dto.LoginResponseDto;

@Service
public class AuthSessionService {

    private final JwtService jwtService;

    public AuthSessionService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public LoginResponseDto getSessionFromAuthorizationHeader(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        return getSessionFromToken(token);
    }

    public LoginResponseDto getSessionFromToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token requerido");
        }

        if (!jwtService.isTokenValid(token)) {
            throw new IllegalArgumentException("Token invalido o expirado");
        }

        UUID userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new IllegalArgumentException("Token sin user_id valido");
        }

        LoginResponseDto response = new LoginResponseDto();
        response.setAccessToken(token);
        response.setUserId(userId.toString());
        response.setEmail(jwtService.extractEmail(token));
        response.setRole(normalizeRole(jwtService.extractRole(token)));
        return response;
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("Header Authorization requerido");
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization debe ser Bearer token");
        }

        return authorizationHeader.substring(7);
    }

    private String normalizeRole(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            return "USER";
        }

        String roleUpper = rawRole.trim().toUpperCase(Locale.ROOT);
        if ("AUTHENTICATED".equals(roleUpper) || "USER".equals(roleUpper)) {
            return "USER";
        }
        if ("ADMIN".equals(roleUpper) || "SERVICE_ROLE".equals(roleUpper)) {
            return "ADMIN";
        }
        return roleUpper;
    }
}
