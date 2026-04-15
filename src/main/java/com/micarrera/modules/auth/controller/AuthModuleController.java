package com.micarrera.modules.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.micarrera.modules.auth.api.dto.ChangePasswordRequestDto;
import com.micarrera.modules.auth.api.dto.LoginRequestDto;
import com.micarrera.modules.auth.api.dto.LoginResponseDto;
import com.micarrera.modules.auth.api.dto.RegisterRequestDto;
import com.micarrera.modules.auth.api.dto.TokenValidationRequestDto;
import com.micarrera.modules.auth.api.AuthSessionApi;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthModuleController {

    private static final String SUPABASE_MANAGED_MESSAGE = "Operacion gestionada por Supabase Auth";

    private final AuthSessionApi authSessionService;

    @GetMapping("/me")
    public LoginResponseDto me(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return authSessionService.getSessionFromAuthorizationHeader(authorizationHeader);
    }

    @PostMapping("/token/validate")
    public LoginResponseDto validateToken(@RequestBody TokenValidationRequestDto request) {
        return authSessionService.getSessionFromToken(request.getAccessToken());
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody(required = false) LoginRequestDto request) {
        return supabaseManaged("login");
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody(required = false) RegisterRequestDto request) {
        return supabaseManaged("register");
    }

    @PostMapping("/register-admin")
    public ResponseEntity<Map<String, String>> registerAdmin(
            @RequestBody(required = false) RegisterRequestDto request) {
        return supabaseManaged("register-admin");
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody(required = false) ChangePasswordRequestDto request) {
        return supabaseManaged("change-password");
    }

    private ResponseEntity<Map<String, String>> supabaseManaged(String operation) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(Map.of("message", SUPABASE_MANAGED_MESSAGE, "operation", operation));
    }
}
