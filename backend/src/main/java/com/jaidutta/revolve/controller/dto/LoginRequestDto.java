package com.jaidutta.revolve.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class LoginRequestDto {
    private String username;

    private String password;

    public LoginRequestDto() {}

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}