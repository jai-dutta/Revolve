    package com.jaidutta.revolve.controller;

    import com.jaidutta.revolve.controller.dto.ApiResponseDto;
    import com.jaidutta.revolve.controller.dto.LoginRequestDto;
    import com.jaidutta.revolve.controller.dto.RegisterRequestDto;
    import com.jaidutta.revolve.repository.UserRepository;
    import com.jaidutta.revolve.service.AuthService; // Assuming AuthService might be needed, added import
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

        private void assertErrorResponse(ResponseEntity<ApiResponseDto> response,
                                         HttpStatus expectedHttpStatus,
                                         String expectedErrorField) {
            assertEquals(expectedHttpStatus, response.getStatusCode());

            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors(), "Errors list should not be null for an error response");
            assertFalse(response.getBody().getErrors().isEmpty(), "Errors list should not be empty for an error response");

            Object errorObject = response.getBody().getErrors().getFirst();
            assertTrue(errorObject instanceof ApiResponseDto.ErrorDto, "Error object should be of type ErrorDto");
            ApiResponseDto.ErrorDto dto = (ApiResponseDto.ErrorDto) errorObject;

            assertEquals(expectedErrorField, dto.getField(), "Error field mismatch");
        }


        @Nested
        class RegistrationTests {

            @Test
            void should_returnCreatedStatus_when_registeringNewUser() {
                ResponseEntity<ApiResponseDto> response = registerUser("username", "password");
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
            }

            @Test
            void should_returnCreatedStatus_when_registeringNewUserWithHyphenatedUsername() {
                ResponseEntity<ApiResponseDto> response = registerUser("user-name", "password");
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
            }

            @Test
            void should_returnCreatedStatus_when_registeringNewUserWithUnderscoredUsername() {
                ResponseEntity<ApiResponseDto> response = registerUser("user_name", "password");
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
            }

            @Test
            void should_returnBadRequest_when_registeringDuplicateUsername() {
                registerUser("username", "password"); // First registration should succeed
                ResponseEntity<ApiResponseDto> response2 = registerUser("username", "password"); // Second attempt

                // Use helper for BAD_REQUEST related to the 'username' field
                assertErrorResponse(response2, HttpStatus.BAD_REQUEST, "username");
            }

            @Test
            void should_returnBadRequest_when_registeringDuplicateUsernameButWithCapitalisedLetter() {
                registerUser("username", "password"); // First registration should succeed
                ResponseEntity<ApiResponseDto> response2 = registerUser("USERNAME", "password"); // Second attempt with different case

                // Use helper for BAD_REQUEST related to the 'username' field
                assertErrorResponse(response2, HttpStatus.BAD_REQUEST, "username");
            }

            @Test
            void should_returnBadRequest_when_passwordTooShort() {
                ResponseEntity<ApiResponseDto> response = registerUser("username", "pass");
                // Use helper for BAD_REQUEST related to the 'password' field (validation)
                assertErrorResponse(response, HttpStatus.BAD_REQUEST, "password");
            }

            @Test
            void should_returnBadRequest_when_passwordTooLong() {
                ResponseEntity<ApiResponseDto> response = registerUser("username", "passwordpasswordpasswordpassword-passwordpasswordpasswordpassword");
                // Use helper for BAD_REQUEST related to the 'password' field (validation)
                assertErrorResponse(response, HttpStatus.BAD_REQUEST, "password");
            }

            @Test
            void should_returnBadRequest_when_usernameTooLong() {
                ResponseEntity<ApiResponseDto> response = registerUser("usernameusernameusernameusername-usernameusernameusernameusername", "password");
                // Use helper for BAD_REQUEST related to the 'username' field (validation)
                assertErrorResponse(response, HttpStatus.BAD_REQUEST, "username");
            }

            @Test
            void should_returnBadRequest_when_usernameTooShort() {
                ResponseEntity<ApiResponseDto> response = registerUser("use", "password");
                // Use helper for BAD_REQUEST related to the 'username' field (validation)
                assertErrorResponse(response, HttpStatus.BAD_REQUEST, "username");
            }

            @Test
            void should_returnBadRequest_when_usernameContainsSpace() {
                ResponseEntity<ApiResponseDto> response = registerUser("user name", "password");

                // Use helper for BAD_REQUEST related to the 'username' field (validation)
                assertErrorResponse(response, HttpStatus.BAD_REQUEST, "username");
            }

            @Test
            void should_returnBadRequest_when_usernameContainsBackslash() {
                ResponseEntity<ApiResponseDto> response = registerUser("user\name", "password");

                // Use helper for BAD_REQUEST related to the 'username' field (validation)
                assertErrorResponse(response, HttpStatus.BAD_REQUEST, "username");
            }

            @Test
            void should_returnBadRequest_when_usernameContainsSpecialCharacter() {
                ResponseEntity<ApiResponseDto> response = registerUser("user$name", "password");

                // Use helper for BAD_REQUEST related to the 'username' field (validation)
                assertErrorResponse(response, HttpStatus.BAD_REQUEST, "username");
            }

            @Test
            void should_returnBadRequest_when_usernameIsBlank() {
                ResponseEntity<ApiResponseDto> response = registerUser("", "password");

                // Use helper for BAD_REQUEST related to the 'username' field (validation)
                assertErrorResponse(response, HttpStatus.BAD_REQUEST, "username");
            }

            @Test
            void should_returnBadRequest_when_usernameIsWhitespace() {
                ResponseEntity<ApiResponseDto> response = registerUser("   ", "password");

                // Use helper for BAD_REQUEST related to the 'username' field (validation)
                assertErrorResponse(response, HttpStatus.BAD_REQUEST, "username");
            }
            @Test
            void should_returnBadRequest_when_usernameContainsWhitespace() {
                ResponseEntity<ApiResponseDto> response = registerUser("user name", "password");

                // Use helper for BAD_REQUEST related to the 'username' field (validation)
                assertErrorResponse(response, HttpStatus.BAD_REQUEST, "username");
            }
        }

        @Nested
        class LoginTests {
            @Test
            void should_returnOkStatus_when_loggingInToAccountThatExists() {
                registerUser("username", "password");
                ResponseEntity<ApiResponseDto> response = loginUser("username", "password");

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody()); // Ensure the body isn't null for a success case
                assertNull(response.getBody().getErrors(), "Errors should be null for successful login");
            }

            @Test
            void should_returnOkStatus_when_loggingInToAccountThatExistsWithCapitalisedUsername() {
                registerUser("username", "password");
                ResponseEntity<ApiResponseDto> response = loginUser("USERNAME", "password");

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody()); // Ensure the body isn't null for a success case
                assertNull(response.getBody().getErrors(), "Errors should be null for successful login with capitalized username");
            }

            @Test
            void should_returnUnauthorized_when_loggingInToAccountThatDoesNotExist() {
                ResponseEntity<ApiResponseDto> response = loginUser("nonexistentuser", "password"); // Use a clearly non-existent user

                // Call the helper method with the expected status and error field
                assertErrorResponse(response, HttpStatus.UNAUTHORIZED, "auth.error");
            }

            @Test
            void should_returnUnauthorized_when_loggingInToAccountWithWrongPassword() {
                registerUser("username", "password");
                ResponseEntity<ApiResponseDto> response = loginUser("username", "incorrectPassword");

                // Call the helper method
                assertErrorResponse(response, HttpStatus.UNAUTHORIZED, "auth.error");
            }

            @Test
            void should_returnUnauthorized_when_loggingInToAccountWithWrongPasswordCausedByCapitalLetter() {
                registerUser("username", "password");
                ResponseEntity<ApiResponseDto> response = loginUser("username", "paSsword");

                // Call the helper method
                assertErrorResponse(response, HttpStatus.UNAUTHORIZED, "auth.error");
            }

            @Test
            void should_returnUnauthorized_when_loggingInToAccountWithWrongUsername() {
                registerUser("username", "password");
                ResponseEntity<ApiResponseDto> response = loginUser("incorrectUsername", "password");

                // Call the helper method
                assertErrorResponse(response, HttpStatus.UNAUTHORIZED, "auth.error");
            }

            @Test
            void should_returnUnauthorized_when_loggingInToAccountWitEmptyPassword() {
                // Registering a user is needed to test authentication failure
                registerUser("username", "password");
                ResponseEntity<ApiResponseDto> response = loginUser("username", ""); // Test with the correct username, empty password

                // Call the helper method
                assertErrorResponse(response, HttpStatus.UNAUTHORIZED, "auth.error");
            }

            @Test
            void should_returnUnauthorized_when_loggingInToAccountWitEmptyUsername() {
                // Registering a user is needed to test authentication failure
                registerUser("username", "password");
                ResponseEntity<ApiResponseDto> response = loginUser("", "password"); // Test with empty username, correct password

                // Call the helper method
                assertErrorResponse(response, HttpStatus.UNAUTHORIZED, "auth.error");
            }
        }
    }