package com.jaidutta.revolve.controller;

import com.jaidutta.revolve.controller.dto.ApiResponseDto;
import com.jaidutta.revolve.controller.dto.AuthDto;
import com.jaidutta.revolve.controller.dto.LoginRequestDto;
import com.jaidutta.revolve.controller.dto.RegisterRequestDto;
import com.jaidutta.revolve.exception.NonUniqueUsernameException;
import com.jaidutta.revolve.security.JwtUtils;
import com.jaidutta.revolve.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/auth") public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired public AuthController(AuthService authService,
                                     AuthenticationManager authenticationManager,
                                     JwtUtils jwtUtils) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register") public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequestDto registerRequestDto)
            throws NonUniqueUsernameException {
        authService.registerUser(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ApiResponseDto.success("Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),
                                                        loginRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateToken(authentication);
        AuthDto authDto = new AuthDto(jwt);

        return ResponseEntity.ok(ApiResponseDto.success(authDto));
    }
}
