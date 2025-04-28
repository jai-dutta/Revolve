package com.jaidutta.revolve.service;

import com.jaidutta.revolve.controller.dto.RegisterRequestDto;
import com.jaidutta.revolve.entity.User;
import com.jaidutta.revolve.exception.NonUniqueUsernameException;
import com.jaidutta.revolve.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isUserRegistered(String username) {
        return userRepository.findByUsernameIgnoreCase(username).isPresent();
    }

    public void registerUser(RegisterRequestDto registerRequestDto) throws NonUniqueUsernameException {
        String username = registerRequestDto.getUsername();
        String password = registerRequestDto.getPassword();

        if (isUserRegistered(username)) {
            throw new NonUniqueUsernameException("User already registered");
        }

        User newUser = new User(username, passwordEncoder.encode(password));
        userRepository.save(newUser);

    }
}
