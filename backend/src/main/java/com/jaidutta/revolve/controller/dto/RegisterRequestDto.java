package com.jaidutta.revolve.controller.dto;


public class RegisterRequestDto {

    private String username;
    private String password;

    public RegisterRequestDto() {}

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
