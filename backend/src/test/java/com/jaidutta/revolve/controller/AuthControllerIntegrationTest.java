package com.jaidutta.revolve.controller;

import com.jaidutta.revolve.controller.dto.ApiResponseDto;
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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
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
        void should_returnCreatedStatus_when_registeringNewUser() {
            ResponseEntity<ApiResponseDto> response = registerUser("username", "password");

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }

        @Test
        void should_returnBadRequest_when_registeringDuplicateUsername() {
            ResponseEntity<ApiResponseDto> response = registerUser("username", "password");

            assertEquals(HttpStatus.CREATED, response.getStatusCode());

            ResponseEntity<ApiResponseDto> response2 = registerUser("username", "password");

            List<ApiResponseDto.ErrorDto> errors = response2.getBody().getErrors();

            // Assert that the response code is BAD_REQUEST
            assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

            assertEquals(1, errors.size());

            assertEquals("auth.username", errors.getFirst().getField());
            assertEquals("Username already exists", errors.getFirst().getMessage());
        }

        // Todo: Fix rest of test cases
        
        @Test
        void should_returnBadRequest_when_passwordTooShort() {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                    "integ-test",
                    "pass");

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    Map.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().containsValue("Password must be between 8 and 64 characters"));
        }

        @Test
        void should_returnBadRequest_when_passwordTooLong() {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                    "integ-test",
                    "passwordpasswordpasswordpassword-passwordpasswordpasswordpassword"); // 65 chars (max 64)

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    Map.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().containsValue("Password must be between 8 and 64 characters"));
        }

        @Test
        void should_returnBadRequest_when_usernameTooLong() {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                    "usernameusernameusernameusername-usernameusernameusernameusername", // 65 chars (max 64)
                    "password"
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    Map.class
            );

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().containsValue("Username must be between 4 and 64 characters"));
        }

        @Test
        void should_returnBadRequest_when_usernameTooShort() {
            RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                    "use",
                    "password"
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    registerRequestDto,
                    Map.class
            );

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().containsValue("Username must be between 4 and 64 characters"));
        }
    }

    @Nested
    class LoginTests {
        @Test
        void should_returnOkStatus_when_loggingInToAccountThatExists() {
            assertEquals(HttpStatus.CREATED,
                    registerUser("username", "password").getStatusCode());

            LoginRequestDto loginRequestDto = new LoginRequestDto("username", "password");
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/auth/login",
                    loginRequestDto,
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    private ResponseEntity<ApiResponseDto> registerUser(String username, String password) {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                username,
                password
        );

        return restTemplate.postForEntity(
                "/api/auth/register",
                registerRequestDto,
                ApiResponseDto.class
        );
    }
}