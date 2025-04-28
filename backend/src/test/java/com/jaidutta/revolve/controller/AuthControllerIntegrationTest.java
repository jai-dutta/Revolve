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
            assertNotNull(response2.getBody());
            List<ApiResponseDto.ErrorDto> errors = response2.getBody().getErrors();

            // Assert that the response code is BAD_REQUEST
            assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
            assertEquals(1, errors.size());
            assertEquals("username", errors.getFirst().getField());
        }

        // Todo: Fix rest of test cases
        
        @Test
        void should_returnBadRequest_when_passwordTooShort() {
            ResponseEntity<ApiResponseDto> response = registerUser("username", "pass");

            assertNotNull(response.getBody());
            List<ApiResponseDto.ErrorDto> errors = response.getBody().getErrors();


            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            assertEquals(1, errors.size());
            assertEquals("password", errors.getFirst().getField());
        }

        @Test
        void should_returnBadRequest_when_passwordTooLong() {
            ResponseEntity<ApiResponseDto> response = registerUser("username", "passwordpasswordpasswordpassword-passwordpasswordpasswordpassword");

            assertNotNull(response.getBody());
            List<ApiResponseDto.ErrorDto> errors = response.getBody().getErrors();


            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            assertEquals(1, errors.size());
            assertEquals("password", errors.getFirst().getField());
        }

        @Test
        void should_returnBadRequest_when_usernameTooLong() {
            ResponseEntity<ApiResponseDto> response = registerUser("usernameusernameusernameusername-usernameusernameusernameusername", "password");

            assertNotNull(response.getBody());
            List<ApiResponseDto.ErrorDto> errors = response.getBody().getErrors();


            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            assertEquals(1, errors.size());
            assertEquals("username", errors.getFirst().getField());
        }

        @Test
        void should_returnBadRequest_when_usernameTooShort() {
            ResponseEntity<ApiResponseDto> response = registerUser("use", "password");

            assertNotNull(response.getBody());
            List<ApiResponseDto.ErrorDto> errors = response.getBody().getErrors();


            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            assertEquals(1, errors.size());
            assertEquals("username", errors.getFirst().getField());
        }
    }

    @Nested
    class LoginTests {
        @Test
        void should_returnOkStatus_when_loggingInToAccountThatExists() {
            registerUser("username", "password");
            ResponseEntity<ApiResponseDto> response = loginUser("username", "password");

            assertNull(response.getBody().getErrors());
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

    private ResponseEntity<ApiResponseDto> loginUser(String username, String password) {
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                username,
                password
        );

        return restTemplate.postForEntity(
                "/api/auth/login",
                loginRequestDto,
                ApiResponseDto.class
        );
    }
}