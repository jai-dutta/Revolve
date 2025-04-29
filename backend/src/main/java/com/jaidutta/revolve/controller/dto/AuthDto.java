package com.jaidutta.revolve.controller.dto;

public class AuthDto {
    private String accessToken;
    private String tokenType = "Bearer";

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
