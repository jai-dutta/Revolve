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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
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

            assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());


            assertNotNull(response2.getBody());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response2.getBody().getErrors().getFirst();

            assertEquals("username", dto.getField());
        }

        @Test
        void should_returnBadRequest_when_registeringDuplicateUsernameButWithCapitalisedLetter() {
            ResponseEntity<ApiResponseDto> response = registerUser("username", "password");
            assertEquals(HttpStatus.CREATED, response.getStatusCode());

            ResponseEntity<ApiResponseDto> response2 = registerUser("USERNAME", "password");

            assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());


            assertNotNull(response2.getBody());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response2.getBody().getErrors().getFirst();

            assertEquals("username", dto.getField());
        }

        @Test
        void should_returnBadRequest_when_passwordTooShort() {
            ResponseEntity<ApiResponseDto> response = registerUser("username", "pass");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            assertNotNull(response.getBody());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response.getBody().getErrors().getFirst();

            assertEquals("password", dto.getField());
        }

        @Test
        void should_returnBadRequest_when_passwordTooLong() {
            ResponseEntity<ApiResponseDto> response = registerUser("username", "passwordpasswordpasswordpassword-passwordpasswordpasswordpassword");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            assertNotNull(response.getBody());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response.getBody().getErrors().getFirst();

            assertEquals("password", dto.getField());
        }

        @Test
        void should_returnBadRequest_when_usernameTooLong() {
            ResponseEntity<ApiResponseDto> response = registerUser("usernameusernameusernameusername-usernameusernameusernameusername", "password");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            assertNotNull(response.getBody());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response.getBody().getErrors().getFirst();

            assertEquals("username", dto.getField());
        }

        @Test
        void should_returnBadRequest_when_usernameTooShort() {
            ResponseEntity<ApiResponseDto> response = registerUser("use", "password");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            assertNotNull(response.getBody());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response.getBody().getErrors().getFirst();

            assertEquals("username", dto.getField());
        }
    }

    @Nested
    class LoginTests {
        @Test
        void should_returnOkStatus_when_loggingInToAccountThatExists() {
            registerUser("username", "password");
            ResponseEntity<ApiResponseDto> response = loginUser("username", "password");

            assertEquals(HttpStatus.OK, response.getStatusCode());

            assertNull(response.getBody().getErrors());
        }

        @Test
        void should_returnOkStatus_when_loggingInToAccountThatExistsWithCapitalisedUsername() {
            registerUser("username", "password");
            ResponseEntity<ApiResponseDto> response = loginUser("USERNAME", "password");

            assertEquals(HttpStatus.OK, response.getStatusCode());

            assertNull(response.getBody().getErrors());
        }

        @Test
        void should_returnUnauthorized_when_loggingInToAccountThatDoesNotExist() {

            ResponseEntity<ApiResponseDto> response = loginUser("username", "incorrectPassword");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

            assertNotNull(response.getBody().getErrors());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response.getBody().getErrors().getFirst();

            assertEquals("auth.error", dto.getField());
        }

        @Test
        void should_returnUnauthorized_when_loggingInToAccountWithWrongPassword() {
            registerUser("username", "password");
            ResponseEntity<ApiResponseDto> response = loginUser("username", "incorrectPassword");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

            assertNotNull(response.getBody().getErrors());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response.getBody().getErrors().getFirst();

            assertEquals("auth.error", dto.getField());
        }

        @Test
        void should_returnUnauthorized_when_loggingInToAccountWithWrongPasswordCausedByCapitalLetter() {
            registerUser("username", "password");
            ResponseEntity<ApiResponseDto> response = loginUser("username", "paSsword");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

            assertNotNull(response.getBody().getErrors());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response.getBody().getErrors().getFirst();

            assertEquals("auth.error", dto.getField());
        }

        @Test
        void should_returnUnauthorized_when_loggingInToAccountWithWrongUsername() {
            registerUser("username", "password");
            ResponseEntity<ApiResponseDto> response = loginUser("incorrectUsername", "password");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

            assertNotNull(response.getBody().getErrors());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response.getBody().getErrors().getFirst();

            assertEquals("auth.error", dto.getField());
        }

        @Test
        void should_returnUnauthorized_when_loggingInToAccountWitEmptyPassword() {
            registerUser("username", "password");
            ResponseEntity<ApiResponseDto> response = loginUser("incorrectUsername", "");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

            assertNotNull(response.getBody().getErrors());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response.getBody().getErrors().getFirst();

            assertEquals("auth.error", dto.getField());
        }

        @Test
        void should_returnUnauthorized_when_loggingInToAccountWitEmptyUsername() {
            registerUser("username", "password");
            ResponseEntity<ApiResponseDto> response = loginUser("", "password");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

            assertNotNull(response.getBody().getErrors());
            ApiResponseDto.ErrorDto dto =
                    (ApiResponseDto.ErrorDto) response.getBody().getErrors().getFirst();

            assertEquals("auth.error", dto.getField());
        }
        
    }
}