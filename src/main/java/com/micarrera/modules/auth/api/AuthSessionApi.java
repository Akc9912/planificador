package com.micarrera.modules.auth.api;

import com.micarrera.modules.auth.api.dto.LoginResponseDto;

public interface AuthSessionApi {
    LoginResponseDto getSessionFromAuthorizationHeader(String authorizationHeader);
    LoginResponseDto getSessionFromToken(String token);
}
