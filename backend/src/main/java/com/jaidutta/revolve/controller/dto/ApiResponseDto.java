package com.jaidutta.revolve.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {

    private final T data;
    private final List<ErrorDto> errors;
    private final String message;

    // Private constructor
    private ApiResponseDto(T data, List<ErrorDto> errors, String message) {
        this.data = data;
        this.errors = (errors != null && !errors.isEmpty()) ? new ArrayList<>(errors) : null;
        this.message = message;
    }

    // Static factory for success responses with data
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(data, null, null);
    }

    // Static factory for success responses with just a message
    public static <T> ApiResponseDto<T> success(String message) {
        return new ApiResponseDto<>(null, null, message);
    }

    // Static factory for a single error
    public static <T> ApiResponseDto<T> error(ErrorDto error) {
        return new ApiResponseDto<>(null, List.of(error), null);
    }

    // Static factory for multiple errors
    public static <T> ApiResponseDto<T> errors(List<ErrorDto> errors) {
        return new ApiResponseDto<>(null, errors, null);
    }
    // Static factory for a simple single error message
    public static <T> ApiResponseDto<T> error(String field, String message) {
        return new ApiResponseDto<>(null, List.of(new ErrorDto(field, message)), null);
    }


    public T getData() {
        return data;
    }

    public List<ApiResponseDto.ErrorDto> getErrors() {
        return errors;
    } // Returns List

    public String getMessage() {
        return message;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDto {
        private String field;
        private String message;


        private ErrorDto() {
        }

        public ErrorDto(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public ErrorDto(String message) {
            this(null, message);
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

    }
}