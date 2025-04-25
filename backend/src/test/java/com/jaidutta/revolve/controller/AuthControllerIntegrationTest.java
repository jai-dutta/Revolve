package com.jaidutta.revolve.controller;

import com.jaidutta.revolve.controller.dto.LoginRequestDto;
import com.jaidutta.revolve.controller.dto.RegisterRequestDto;
import com.jaidutta.revolve.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl); // Use method reference
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }


    @Nested
    class RegistrationTests {

        @Test
        void should_returnCreatedStatus_when_registeringNewUser () {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                    "integ-test",
                    "password");

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    String.class);

            assert (response.getStatusCode().equals(HttpStatus.CREATED));
        }

        @Test
        void should_returnBadRequest_when_registeringDuplicateUsername () {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                    "integ-test",
                    "password");

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    String.class);

            assert (response.getStatusCode().equals(HttpStatus.CREATED));

            registerRequestDto = new RegisterRequestDto(
                    "integ-test",
                    "password");

            response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    String.class);

            assert (response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        }

        @Test
        void should_returnBadRequest_when_passwordTooShort () {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                    "integ-test",
                    "pass");

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    Map.class);

            assert (response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
            assert (response.getBody() != null && response.getBody().containsValue("Password must be between 8 and 64 " +
                    "characters"));
        }

        @Test
        void should_returnBadRequest_when_passwordTooLong () {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                    "integ-test",
                    "passwordpasswordpasswordpassword-passwordpasswordpasswordpassword"); // 65 chars (max 64)

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    Map.class);

            assert (response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
            assert (response.getBody() != null && response.getBody().containsValue("Password must be between 8 and 64 " +
                    "characters"));
        }

        @Test
        void should_returnBadRequest_when_usernameTooLong () {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                    "usernameusernameusernameusername-usernameusernameusernameusername", // 65 chars (max 64)
                    "password"
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    Map.class
            );

            assert (response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
            assert (response.getBody() != null && response.getBody().containsValue("Username must be between 4 and 64 " +
                    "characters"));
        }

        @Test
        void should_returnBadRequest_when_usernameTooShort () {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                    "use",
                    "password"
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    Map.class
            );

            assert (response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
            assert (response.getBody() != null && response.getBody().containsValue("Username must be between 4 and 64 " +
                    "characters"));
        }
    }

    @Test
    void testLoginSuccessful() {
        assert (registerEntitySuccessfully("username", "password")
                .getStatusCode().equals(HttpStatus.CREATED));

        LoginRequestDto loginRequestDto = new LoginRequestDto("username", "password");
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                loginRequestDto,
                String.class
        );

        assert (response.getStatusCode().equals(HttpStatus.OK));

    }

    private ResponseEntity<String> registerEntitySuccessfully(String username, String password) {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                username,
                password
        );

       return restTemplate.postForEntity(
                "/api/auth/register",
                registerRequestDto,
                String.class
        );
    }
}
