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
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers public class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine");
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

    @BeforeEach void setup() {
        userRepository.deleteAll();
    }

    private ResponseEntity<ApiResponseDto> registerUser(String username, String password) {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(username,
                                                                       password);

        return restTemplate.postForEntity("/api/auth/register", registerRequestDto,
                                          ApiResponseDto.class);
    }

    private ResponseEntity<ApiResponseDto> loginUser(String username, String password) {
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        return restTemplate.postForEntity("/api/auth/login", loginRequestDto,
                                          ApiResponseDto.class);
    }

    @Nested class meTests {
        @Test public void should_returnCurrentUserDetails() {
            registerUser("username", "password");
            ResponseEntity<ApiResponseDto> response = loginUser("username", "password");
            assertInstanceOf(LinkedHashMap.class, response.getBody().getData());

            LinkedHashMap responseBody = (LinkedHashMap) response.getBody().getData();

            String jwtToken = (String) responseBody.get("accessToken");

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwtToken);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<ApiResponseDto> userResponse = restTemplate.exchange(
                    "/api/users/me",  // The endpoint URL
                    HttpMethod.GET, requestEntity, ApiResponseDto.class
                    // The response type
                                                                               );

            assertEquals(HttpStatus.OK, userResponse.getStatusCode());
            assertNotNull(userResponse.getBody());
            assertEquals(userResponse.getBody().getMessage(), "Hello username");
        }

        @Test public void should_returnCurrentUserDetailsWithCapitalisedUsername() {
            registerUser("uSeRnAmE", "password");
            ResponseEntity<ApiResponseDto> response = loginUser("username", "password");
            assertInstanceOf(LinkedHashMap.class, response.getBody().getData());

            LinkedHashMap responseBody = (LinkedHashMap) response.getBody().getData();

            String jwtToken = (String) responseBody.get("accessToken");

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwtToken);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<ApiResponseDto> userResponse = restTemplate.exchange(
                    "/api/users/me",  // The endpoint URL
                    HttpMethod.GET, requestEntity, ApiResponseDto.class
                    // The response type
                                                                               );

            assertEquals(HttpStatus.OK, userResponse.getStatusCode());
            assertNotNull(userResponse.getBody());
            assertEquals("Hello uSeRnAmE", userResponse.getBody().getMessage());
        }
    }
}
