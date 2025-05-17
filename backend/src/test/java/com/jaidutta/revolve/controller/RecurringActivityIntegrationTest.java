package com.jaidutta.revolve.controller;

import com.jaidutta.revolve.controller.dto.*;
import com.jaidutta.revolve.definitions.ActivityType;
import com.jaidutta.revolve.repository.RecurringActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) @Testcontainers
public class RecurringActivityIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private RecurringActivityRepository recurringActivityRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PASSWORD = "testPassword";
    private String accessToken;

    @DynamicPropertySource static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach void setup() {
        recurringActivityRepository.deleteAll();

        // Register a test user
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(TEST_USERNAME, TEST_PASSWORD);
        restTemplate.postForEntity("/api/auth/register", registerRequestDto, ApiResponseDto.class);

        // Login to get JWT token
        LoginRequestDto loginRequestDto = new LoginRequestDto(TEST_USERNAME, TEST_PASSWORD);
        ResponseEntity<ApiResponseDto> loginResponse = restTemplate.postForEntity("/api/auth/login", loginRequestDto, ApiResponseDto.class);

        // Extract JWT token from login response
        LinkedHashMap responseBody = (LinkedHashMap) loginResponse.getBody().getData();

        accessToken = (String) responseBody.get("accessToken");
        }

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    private RecurringActivityDto createSampleActivityDto() {
        RecurringActivityDto dto = new RecurringActivityDto();
        dto.setActivityName("Test Activity");
        dto.setCourseName("Test Course");
        dto.setActivityType(ActivityType.LECTURE);
        dto.setDayOfWeek(DayOfWeek.MONDAY);
        dto.setStartTime(LocalTime.now());
        dto.setDurationMinutes(120);
        return dto;
    }

    private ResponseEntity<ApiResponseDto> createRecurringActivity(RecurringActivityDto dto) {
        HttpEntity<RecurringActivityDto> request = new HttpEntity<>(dto, getAuthHeaders());
        return restTemplate.postForEntity("/api/recurring-activities/create", request, ApiResponseDto.class);
    }

    @Nested class CreateRecurringActivityTests {

        @Test
        public void should_returnCreatedStatus_whenRegisteringRecurringActivity() {
            RecurringActivityDto dto = createSampleActivityDto();

            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getData());
        }


    }
}
