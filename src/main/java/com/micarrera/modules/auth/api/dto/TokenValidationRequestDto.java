package com.micarrera.modules.auth.api.dto;

public class TokenValidationRequestDto {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
