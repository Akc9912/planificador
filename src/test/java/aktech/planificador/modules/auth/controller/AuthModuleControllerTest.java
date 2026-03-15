package aktech.planificador.modules.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import aktech.planificador.modules.auth.dto.ChangePasswordRequestDto;
import aktech.planificador.modules.auth.dto.LoginRequestDto;
import aktech.planificador.modules.auth.dto.LoginResponseDto;
import aktech.planificador.modules.auth.dto.RegisterRequestDto;
import aktech.planificador.modules.auth.dto.TokenValidationRequestDto;
import aktech.planificador.modules.auth.service.AuthSessionService;

@ExtendWith(MockitoExtension.class)
class AuthModuleControllerTest {

    @Mock
    private AuthSessionService authSessionService;

    private AuthModuleController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthModuleController(authSessionService);
    }

    @Test
    void me_shouldDelegateToSessionService() {
        String header = "Bearer some-token";
        LoginResponseDto expected = new LoginResponseDto();
        expected.setUserId("user-id");

        when(authSessionService.getSessionFromAuthorizationHeader(header)).thenReturn(expected);

        LoginResponseDto response = controller.me(header);

        assertSame(expected, response);
        verify(authSessionService).getSessionFromAuthorizationHeader(header);
    }

    @Test
    void validateToken_shouldDelegateToSessionService() {
        TokenValidationRequestDto request = new TokenValidationRequestDto();
        request.setAccessToken("token-123");

        LoginResponseDto expected = new LoginResponseDto();
        expected.setRole("USER");

        when(authSessionService.getSessionFromToken("token-123")).thenReturn(expected);

        LoginResponseDto response = controller.validateToken(request);

        assertSame(expected, response);
        verify(authSessionService).getSessionFromToken("token-123");
    }

    @Test
    void login_shouldReturnGone() {
        ResponseEntity<Map<String, String>> response = controller.login(new LoginRequestDto());
        assertSupabaseManaged(response, "login");
    }

    @Test
    void register_shouldReturnGone() {
        ResponseEntity<Map<String, String>> response = controller.register(new RegisterRequestDto());
        assertSupabaseManaged(response, "register");
    }

    @Test
    void registerAdmin_shouldReturnGone() {
        ResponseEntity<Map<String, String>> response = controller.registerAdmin(new RegisterRequestDto());
        assertSupabaseManaged(response, "register-admin");
    }

    @Test
    void changePassword_shouldReturnGone() {
        ResponseEntity<Map<String, String>> response = controller.changePassword(new ChangePasswordRequestDto());
        assertSupabaseManaged(response, "change-password");
    }

    private void assertSupabaseManaged(ResponseEntity<Map<String, String>> response, String operation) {
        assertEquals(410, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Operacion gestionada por Supabase Auth", response.getBody().get("message"));
        assertEquals(operation, response.getBody().get("operation"));
    }
}
