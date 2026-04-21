package com.jaidutta.revolve.exception;

import com.jaidutta.revolve.controller.dto.ApiResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException exception) {
        ArrayList<ApiResponseDto.ErrorDto> errors = new ArrayList<>();

        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errors.add(new ApiResponseDto.ErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
        });

        return new ResponseEntity<>(ApiResponseDto.errors(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NonUniqueUsernameException.class)
    public ResponseEntity<Object> handleNonUniqueUsername(NonUniqueUsernameException exception) {
        logger.info("Registration rejected: non-unique username");
        return new ResponseEntity<>(
                ApiResponseDto.error("username", "Username already exists"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception) {
        logger.info("Authentication failed: {}", exception.getMessage());
        return new ResponseEntity<>(
                ApiResponseDto.error("auth.error", "Incorrect username or password"),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RecurringActivityNotFoundException.class)
    public ResponseEntity<Object> handleRecurringActivityNotFoundException(RecurringActivityNotFoundException exception) {
        return new ResponseEntity<>(
                ApiResponseDto.error("activity.find", "Activity not found"),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ActivityInstanceNotFoundException.class)
    public ResponseEntity<Object> handleActivityInstanceNotFoundException(ActivityInstanceNotFoundException exception) {
        return new ResponseEntity<>(
                ApiResponseDto.error("instance.find", "Activity instance not found"),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TooManyRecurringActivitiesRegisteredByUserException.class)
    public ResponseEntity<Object> handleTooManyRecurringActivitiesRegisteredByUserException(TooManyRecurringActivitiesRegisteredByUserException exception) {
        return new ResponseEntity<>(
                ApiResponseDto.error("activity.add", "You have reached the maximum number of activities"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception exception) {
        logger.error("Unhandled exception", exception);
        return new ResponseEntity<>(
                ApiResponseDto.error("server.error", "An unexpected error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
