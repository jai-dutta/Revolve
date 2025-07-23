package com.jaidutta.revolve.controller;

import com.jaidutta.revolve.controller.dto.*;
import com.jaidutta.revolve.definitions.ActivityType;
import com.jaidutta.revolve.repository.RecurringActivityRepository;
import com.jaidutta.revolve.repository.UserRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) @Testcontainers
public class RecurringActivityIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private RecurringActivityRepository recurringActivityRepository;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.deleteAll();
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


    private void assertErrorResponse(ResponseEntity<ApiResponseDto> response, HttpStatus expectedHttpStatus, String expectedErrorField) {
        assertEquals(expectedHttpStatus, response.getStatusCode());

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors(),
                "Errors list should not be null for an error response");
        assertFalse(response.getBody().getErrors().isEmpty(),
                "Errors list should not be empty for an error response");

        Object errorObject = response.getBody().getErrors().getFirst();
        assertInstanceOf(ApiResponseDto.ErrorDto.class, errorObject,
                "Error object should be of type ErrorDto");
        ApiResponseDto.ErrorDto dto = (ApiResponseDto.ErrorDto) errorObject;

        assertEquals(expectedErrorField, dto.getField(), "Error field mismatch");
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

        @Test
        public void should_returnBadRequest_whenActivityNameIsNull() {
            RecurringActivityDto dto = createSampleActivityDto();
            dto.setActivityName(null);

            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors());
        }
        @Test
        public void should_returnBadRequest_whenActivityNameIsEmpty() {
            RecurringActivityDto dto = createSampleActivityDto();
            dto.setActivityName(" ");

            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors());
        }
        @Test
        public void should_returnBadRequest_whenCourseNameIsNull() {
            RecurringActivityDto dto = createSampleActivityDto();
            dto.setCourseName(null);

            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors());
        }
        @Test
        public void should_returnBadRequest_whenCourseNameIsEmpty() {
            RecurringActivityDto dto = createSampleActivityDto();
            dto.setCourseName(" ");

            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors());
        }
        @Test
        public void should_returnBadRequest_whenActivityTypeIsNull() {
            RecurringActivityDto dto = createSampleActivityDto();
            dto.setActivityType(null);

            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors());
        }
        @Test
        public void should_returnBadRequest_whenDayOfWeekIsNull() {
            RecurringActivityDto dto = createSampleActivityDto();
            dto.setDayOfWeek(null);

            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors());
        }
        @Test
        public void should_returnBadRequest_whenStartTimeIsNull() {
            RecurringActivityDto dto = createSampleActivityDto();
            dto.setStartTime(null);

            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors());
        }
        @Test
        public void should_returnBadRequest_whenDurationIsNull() {
            RecurringActivityDto dto = createSampleActivityDto();
            dto.setDurationMinutes(null);

            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors());
        }
        @Test
        public void should_returnBadRequest_whenDurationIsNegative() {
            RecurringActivityDto dto = createSampleActivityDto();
            dto.setDurationMinutes(-5);

            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getErrors());
        }
        @Test
        public void should_returnBadRequest_whenReachedActivityLimit() {
            RecurringActivityDto dto = createSampleActivityDto();

            // Todo: add config instead of magic number 20
            for (int i = 0; i < 20; i++) {
                ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertNotNull(response.getBody());
                assertNotNull(response.getBody().getData());
            }


            ResponseEntity<ApiResponseDto>  response = createRecurringActivity(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertErrorResponse(response, HttpStatus.BAD_REQUEST, "activity.add");

        }
    }
    @Nested class ReadRecurringActivityTests {
        @Test
        public void should_returnOkWithActivities_whenUserHasActivities() {
            // Create multiple activities for the user
            RecurringActivityDto dto1 = createSampleActivityDto();
            dto1.setActivityName("Activity 1");
            dto1.setCourseName("Course 1");
            dto1.setDayOfWeek(DayOfWeek.MONDAY);

            RecurringActivityDto dto2 = createSampleActivityDto();
            dto2.setActivityName("Activity 2");
            dto2.setCourseName("Course 2");
            dto2.setDayOfWeek(DayOfWeek.TUESDAY);

            createRecurringActivity(dto1);
            createRecurringActivity(dto2);

            // Get all activities
            HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());
            ResponseEntity<ApiResponseDto> response = restTemplate.exchange(
                    "/api/recurring-activities",
                    org.springframework.http.HttpMethod.GET,
                    request,
                    ApiResponseDto.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getData());

            // Verify we get a list with 2 activities
            @SuppressWarnings("unchecked")
            java.util.List<LinkedHashMap> activities = (java.util.List<LinkedHashMap>) response.getBody().getData();
            assertEquals(2, activities.size());
        }

        @Test
        public void should_returnOkWithEmptyList_whenUserHasNoActivities() {
            // Don't create any activities

            HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());
            ResponseEntity<ApiResponseDto> response = restTemplate.exchange(
                    "/api/recurring-activities",
                    org.springframework.http.HttpMethod.GET,
                    request,
                    ApiResponseDto.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getData());

            // Verify we get an empty list
            @SuppressWarnings("unchecked")
            java.util.List<LinkedHashMap> activities = (java.util.List<LinkedHashMap>) response.getBody().getData();
            assertEquals(0, activities.size());
        }

        @Test
        public void should_returnUnauthorized_whenNoAuthToken() {
            HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());
            ResponseEntity<ApiResponseDto> response = restTemplate.exchange(
                    "/api/recurring-activities",
                    org.springframework.http.HttpMethod.GET,
                    request,
                    ApiResponseDto.class
            );

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

        @Test
        public void should_returnUnauthorized_whenInvalidAuthToken() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer invalid-token");

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<ApiResponseDto> response = restTemplate.exchange(
                    "/api/recurring-activities",
                    org.springframework.http.HttpMethod.GET,
                    request,
                    ApiResponseDto.class
            );

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

        @Test
        public void should_returnOnlyUserActivities_whenMultipleUsersExist() {
            // Create activity for first user
            RecurringActivityDto dto1 = createSampleActivityDto();
            dto1.setActivityName("User1 Activity");
            createRecurringActivity(dto1);

            // Create second user and their activity
            String secondUserUsername = "testUser2";
            String secondUserPassword = "testPassword2";

            RegisterRequestDto registerRequestDto = new RegisterRequestDto(secondUserUsername, secondUserPassword);
            restTemplate.postForEntity("/api/auth/register", registerRequestDto, ApiResponseDto.class);

            LoginRequestDto loginRequestDto = new LoginRequestDto(secondUserUsername, secondUserPassword);
            ResponseEntity<ApiResponseDto> loginResponse = restTemplate.postForEntity("/api/auth/login", loginRequestDto, ApiResponseDto.class);

            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> responseBody = (LinkedHashMap<String, Object>) loginResponse.getBody().getData();
            String secondUserToken = (String) responseBody.get("accessToken");

            // Create activity for second user
            HttpHeaders secondUserHeaders = new HttpHeaders();
            secondUserHeaders.set("Authorization", "Bearer " + secondUserToken);

            RecurringActivityDto dto2 = createSampleActivityDto();
            dto2.setActivityName("User2 Activity");
            HttpEntity<RecurringActivityDto> secondUserRequest = new HttpEntity<>(dto2, secondUserHeaders);
            restTemplate.postForEntity("/api/recurring-activities/create", secondUserRequest, ApiResponseDto.class);

            // Get activities for first user
            HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());
            ResponseEntity<ApiResponseDto> response = restTemplate.exchange(
                    "/api/recurring-activities",
                    org.springframework.http.HttpMethod.GET,
                    request,
                    ApiResponseDto.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            @SuppressWarnings("unchecked")
            java.util.List<LinkedHashMap> activities = (java.util.List<LinkedHashMap>) response.getBody().getData();
            assertEquals(1, activities.size());

            // Verify it's the correct activity for user1
            LinkedHashMap activity = activities.get(0);
            assertEquals("User1 Activity", activity.get("activityName"));
        }
    }
}
