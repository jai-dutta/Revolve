package com.jaidutta.revolve.controller;

import com.jaidutta.revolve.controller.dto.RegisterRequestDto;
import com.jaidutta.revolve.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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


    @Test
    void testRegistrationSuccess() {
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
    void testDuplicateUsernameRegistration() {
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

}
