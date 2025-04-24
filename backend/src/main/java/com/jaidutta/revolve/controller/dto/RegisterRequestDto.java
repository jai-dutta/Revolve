package com.jaidutta.revolve.controller.dto;


public class RegisterRequestDto {

    private final String username;
    private final String password;

    public RegisterRequestDto(String username, String password) {
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
