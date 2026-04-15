package com.micarrera.modules.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.micarrera.modules.auth.api.dto.LoginResponseDto;
import com.micarrera.modules.auth.api.AuthSessionApi;
import com.micarrera.modules.auth.api.JwtApi;
import com.micarrera.modules.auth.service.AuthSessionServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthSessionApiTest {

    @Mock
    private JwtApi jwtService;

    private AuthSessionApi authSessionService;

    @BeforeEach
    void setUp() {
        authSessionService = new AuthSessionServiceImpl(jwtService);
    }

    @Test
    void getSessionFromToken_shouldReturnSessionForValidToken() {
        String token = "valid-token";
        UUID userId = UUID.randomUUID();

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(userId);
        when(jwtService.extractEmail(token)).thenReturn("user@example.com");
        when(jwtService.extractRole(token)).thenReturn("service_role");

        LoginResponseDto response = authSessionService.getSessionFromToken(token);

        assertEquals(token, response.getAccessToken());
        assertEquals(userId.toString(), response.getUserId());
        assertEquals("user@example.com", response.getEmail());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void getSessionFromAuthorizationHeader_shouldThrowWhenHeaderIsInvalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authSessionService.getSessionFromAuthorizationHeader("Token abc"));

        assertEquals("Authorization debe ser Bearer token", exception.getMessage());
    }

    @Test
    void getSessionFromToken_shouldThrowWhenTokenIsInvalid() {
        String token = "expired-token";
        when(jwtService.isTokenValid(token)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authSessionService.getSessionFromToken(token));

        assertEquals("Token invalido o expirado", exception.getMessage());
    }

    @Test
    void getSessionFromToken_shouldThrowWhenUserIdIsMissing() {
        String token = "token-without-user-id";
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authSessionService.getSessionFromToken(token));

        assertEquals("Token sin user_id valido", exception.getMessage());
    }
}
