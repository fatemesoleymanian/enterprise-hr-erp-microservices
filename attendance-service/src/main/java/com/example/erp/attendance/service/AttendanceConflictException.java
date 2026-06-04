package com.example.erp.attendance.service;

public class AttendanceConflictException extends RuntimeException {

    private final String errorCode;

    public AttendanceConflictException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
