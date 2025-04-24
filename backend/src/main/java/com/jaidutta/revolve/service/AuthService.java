package com.jaidutta.revolve.service;

import com.jaidutta.revolve.controller.dto.LoginRequestDto;
import com.jaidutta.revolve.controller.dto.RegisterRequestDto;
import com.jaidutta.revolve.entity.User;
import com.jaidutta.revolve.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public boolean isUserRegistered(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void registerUser(RegisterRequestDto registerRequestDto) throws IllegalArgumentException {
        String username = registerRequestDto.getUsername();
        String password = registerRequestDto.getPassword();

        if (isUserRegistered(username)) {
            // TO-DO: Custom exception here
            throw new IllegalArgumentException("User already registered");
        }

        User newUser = new User(username, passwordEncoder.encode(password));
        userRepository.save(newUser);

    }

}
