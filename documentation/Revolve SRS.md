# Revolve: Personal Academic Workload Tracker

## Software Requirements Specification

**Version:** 0.1  
**Date:** April 16, 2025  
**Developer:** Jai Dutta

---

## 1. Introduction

### 1.1 Project Overview

Revolve is a minimalist web application I am building to track my academic commitments and help stay on top of coursework. The app features a full-screen calendar with cards representing different academic activities that can be flipped when completed.

### 1.2 Project Description

Revolve is a simplistic, minimal web app that tracks academic calendars and helps students keep on top of incomplete work. It features a full-screen-width calendar with cards representing lectures, tutorials, workshops, and assignment work. As a user completes an item of work, they log in and flip the card, which then grays out for that week. Unflipped cards at week's end automatically move to a backlog section. The backlog displays the total time value of contained work items. Users can also add assignments with time allocations (e.g., 2 hours per week for a specific course assignment). The calender is setup once and then "revolves", e.g. only one week is ever displayed, with the same items on it each week. This aims to simply aid students in tracking what they have and have not completed each week.

---

## 2. Problem Statement

Students can struggle to effectively manage their academic workload across multiple courses, particularly when balancing various commitments like lectures, tutorials, workshops, and assignments. Without a dedicated system to track completion and prioritise unfinished work, they can:

1. Lose track of which course activities I've completed in a given week
2. Fail to recognise accumulated backlog until it becomes overwhelming
3. Have difficulty visualising my total time commitments across all courses
4. Lack clear indicators of which missed activities should be prioritised for catch-up

Revolve aims to solve these challenges by providing a simple, visual academic calendar system that helps track weekly academic commitments, clearly shows what remains unfinished, and manages accumulated backlog with time-based metrics to guide prioritization decisions.

---

## 3. Features & Requirements

### 3.1 Core Functionality

- [ ] Weekly calendar view
- [ ] Activity cards (lectures, tutorials, workshops, assignments)
- [ ] Card "flipping" mechanism to mark completion
- [ ] Automatic backlog generation for unfinished items
- [ ] Time tracking for work items

### 3.2 User Interface

- [ ] Minimalist, clean design
- [ ] Full-width calendar display
- [ ] Visual differentiation between completed/incomplete items
- [ ] Backlog section with time metrics

### 3.3 Technical Requirements

- [ ] Web-based application
- [ ] Basic user authentication
- [ ] Data persistence for user activities
- [ ] Responsive design for multiple devices

---

## 4. Development Plan

### 4.1 Technology Stack

- Frontend: React
- Backend: Java Spring Boot
- Database: AWS RDS (PostgreSQL)
- Cloud deployment: AWS
- CI/CD: AWS CodePipeline + CodeBuild + CodeDeploy

### 4.2 Development Phases

1. Design UI mockups and user flow
2. Implement core calendar functionality
3. Add activity card system
4. Develop backlog tracking
5. Implement user authentication
6. Testing and refinement

### 4.3 Timeline

#### Phase 1 - Backend Development (Java Spring Boot & PostgreSQL)

This phase focuses on building the server-side logic, API, and database interactions necessary to support the Revolve application features. Development will proceed through the following key stages:

**Phase 1.1: Project Initialization and Database Integration**

- **Objective:** Establish a functional Spring Boot backend project configured to communicate reliably with the designated AWS RDS PostgreSQL database instance.
- **Key Tasks:**
    - Initialize the Spring Boot project using Maven or Gradle, including essential dependencies (Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Lombok, Validation, JWT Support Library).
    - Configure the `application.properties` (or `.yml`) file within `src/main/resources/` to specify database connection parameters (URL, username, password) targeting the AWS RDS instance. **Credentials must be externalized using environment variables.**
    - Implement initial run configuration to supply environment variables locally.
    - Verify successful database connectivity upon application startup by observing logs and absence of connection errors. Ensure RDS Security Group permits connection from the development environment.

**Phase 1.2: Authentication Foundation**

- **Objective:** Implement core user registration, secure login functionality, and stateless session management using JSON Web Tokens (JWT).
- **Key Tasks:**
    - Define the `User` JPA entity including necessary fields (ID, email, hashed password, timestamps) and create the corresponding `UserRepository` interface.
    - Configure Spring Security: Implement `UserDetailsService` using `UserRepository`, define `PasswordEncoder` bean (BCrypt), establish initial `SecurityFilterChain` bean defining public (`/api/auth/**`) and protected (`/api/**`) endpoint rules.
    - Implement `POST /api/auth/register` endpoint, including input validation (DTOs) and secure password hashing before saving the user.
    - Implement `POST /api/auth/login` endpoint utilizing Spring Security's `AuthenticationManager` for credential validation.
    - Implement JWT generation service: Create signed JWTs upon successful login containing necessary user claims (e.g., user ID). The JWT secret key must be securely managed via environment variables.
    - Implement JWT validation filter: Create and configure a filter to intercept requests to protected endpoints, validate the incoming JWT (signature, expiration), and establish the authenticated user context (`SecurityContextHolder`).
    - Implement a basic protected test endpoint (e.g., `GET /api/users/me`) to verify the end-to-end authentication and token validation flow.
    - _Testing:_ Develop integration tests covering user registration, login success/failure scenarios, and JWT validation for protected resources.

**Phase 1.3: Core Domain Model Implementation (Activities)**

- **Objective:** Define and implement the data models and API endpoints required for managing user-specific academic activities (recurring templates).
- **Key Tasks:**
    - Define `RecurringActivity` (and optional `Course`) JPA entities with appropriate fields and relationships (linking to `User`). Create associated `JpaRepository` interfaces.
    - Implement protected RESTful API endpoints (Controller layer) providing CRUD (Create, Read, Update, Delete) operations for these entities. Utilize DTOs for request/response payloads and implement input validation.
    - **Enforce User Ownership:** Critically ensure all service and repository logic strictly isolates data access and modification based on the authenticated user's ID retrieved from the security context.
    - _Testing:_ Develop integration tests for all CRUD operations, specifically verifying correct data handling and robust enforcement of user ownership constraints.

**Phase 1.4: Activity Instance Management and Tracking**

- **Objective:** Implement the system for managing specific weekly occurrences of activities derived from templates and tracking their completion status.
- **Key Tasks:**
    - Define the `ActivityInstance` JPA entity (linking to `RecurringActivity` and `User`) including fields for `dueDate`, `isCompleted`, `isBacklogged`, etc. Create the associated `ActivityInstanceRepository`.
    - Design and implement the core logic for generating `ActivityInstance` records based on active `RecurringActivity` templates (consider strategies like scheduled tasks or on-demand generation).
    - Implement the API endpoint (`GET /api/activity-instances`) allowing retrieval of instances filtered by the authenticated user and a specified date range (e.g., for displaying a weekly calendar view).
    - Implement the API endpoint (`PUT /api/activity-instances/{id}/complete`) allowing an authenticated user to mark their own activity instance as completed ("flip card"). Ensure ownership verification.
    - _Testing:_ Develop integration tests verifying instance generation logic, correct retrieval based on user/date filters, and the completion status update mechanism including ownership checks.

**Phase 1.5: Backlog Feature Implementation**

- **Objective:** Implement the specific Revolve feature for identifying, flagging, and retrieving activities that were not completed within their designated week.
- **Key Tasks:**
    - Design and implement the logic (e.g., using `@Scheduled` tasks) to periodically identify past-due `ActivityInstance` records that are not completed and flag them by setting `is_backlogged = true`.
    - Implement the API endpoint (`GET /api/backlog`) to retrieve the list of active backlog items (where `is_backlogged = true` and `is_completed = false`) for the currently authenticated user. Consider including associated time values.
    - _Testing:_ Develop tests verifying the accuracy of the backlog flagging logic and the correct retrieval of backlog items via the API endpoint.

**Phase 1.6: Refinement, Error Handling, and Basic CI Setup**

- **Objective:** Enhance the overall quality, robustness, and maintainability of the backend codebase and establish automated checks.
- **Key Tasks:**
    - Implement consistent, application-wide exception handling using `@ControllerAdvice` to provide meaningful API error responses.
    - Review and enhance input validation across API endpoints using `@Valid` annotations on DTOs.
    - Refactor existing code where necessary to improve clarity, reduce duplication, and adhere to best practices.
    - Configure a basic Continuous Integration (CI) pipeline (e.g., using GitHub Actions or AWS CodeBuild) triggered on code commits to automatically compile the application and execute the test suite.

#### Phase 2 - Frontend Development
TODO

---

## 5. Additional Notes

- Personal project with possible collaboration from select friends
- Non-commercial use
- Focus on simplicity and usability over complex features

---

### 6. Future Enhancements:

