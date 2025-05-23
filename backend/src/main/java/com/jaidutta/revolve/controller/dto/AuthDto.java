package com.jaidutta.revolve.controller.dto;

public class AuthDto {
    private final String tokenType = "Bearer";
    private String accessToken;

    public AuthDto() {
    }

    public AuthDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
