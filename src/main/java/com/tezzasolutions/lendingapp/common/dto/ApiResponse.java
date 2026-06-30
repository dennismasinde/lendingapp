package com.tezzasolutions.lendingapp.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Instant timestamp;
    private String path;
    private Integer statusCode;

    public static <T> ApiResponse<T> success(T data, String message, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .path(path)
                .statusCode(200)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String path) {
        return success(data, "Operation completed successfully", path);
    }

    public static <T> ApiResponse<T> error(String message, String path, Integer statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(Instant.now())
                .path(path)
                .statusCode(statusCode)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Resource created successfully")
                .data(data)
                .timestamp(Instant.now())
                .path(path)
                .statusCode(201)
                .build();
    }
}
