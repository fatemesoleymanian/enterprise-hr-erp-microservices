package com.example.erp.common.api;

import java.time.Instant;

public record ApiResponse<T>(
                             T data,
                             String message,
                             Instant timestamp)
{
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, "Success", Instant.now());
    }
}
