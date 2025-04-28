package com.jaidutta.revolve.exception;

import com.jaidutta.revolve.controller.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Todo: add logging

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException exception) {
        ArrayList<ApiResponseDto.ErrorDto> errors = new ArrayList<>();

        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            ApiResponseDto.ErrorDto errorDto = new ApiResponseDto.ErrorDto(fieldName, errorMessage);
            errors.add(errorDto);
        });

        ApiResponseDto<Object> apiResponseDto = ApiResponseDto.errors(errors);

        return new ResponseEntity<>(apiResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NonUniqueUsernameException.class)
    public ResponseEntity<Object> handleNonUniqueUsername(NonUniqueUsernameException exception) {
        ApiResponseDto<Object> apiResponseDto = ApiResponseDto.error("username", "Username " +
                "already exists");
        return new ResponseEntity<>(apiResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception) {
        ApiResponseDto<String>  apiResponseDto = ApiResponseDto.error(
                "auth.error", "Invalid authentication");

        return new ResponseEntity<>(apiResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception exception) {
        ApiResponseDto<String>  apiResponseDto = ApiResponseDto.error(
                "server.error", "Server error");
        return new ResponseEntity<>(apiResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}