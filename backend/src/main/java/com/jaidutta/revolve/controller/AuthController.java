package com.jaidutta.revolve.controller;

import com.jaidutta.revolve.controller.dto.RegisterRequestDto;
import com.jaidutta.revolve.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping("/register")
    public ResponseEntity<?> register(RegisterRequestDto registerRequestDto) {
        try {
            authService.registerUser(registerRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registration successful.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration unsuccessful: " + e.getMessage());
        }
    }
}
