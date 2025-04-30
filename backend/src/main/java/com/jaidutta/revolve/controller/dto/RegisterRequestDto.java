package com.jaidutta.revolve.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequestDto {

    @NotBlank
    @Size(min = 4, max = 64, message = "Username must be between 4 and 64 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username must only contain alphanumeric " +
            "characters, and underscores \"_\" and hyphens \"-\" ")
    private String username;

    @NotBlank
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;

    public RegisterRequestDto() {
    }

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
