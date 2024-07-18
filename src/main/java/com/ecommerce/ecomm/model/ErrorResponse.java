package com.ecommerce.ecomm.model;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private int code;
    private HttpStatus status;
    private String message;

    // Constructor that takes an integer, a string, and another string
    public ErrorResponse(int code, String status, String message) {
        this.code = code;
        this.status = HttpStatus.valueOf(status);
        this.message = message;
    }

    // Constructor that takes an integer, an HttpStatus, and a string
    public ErrorResponse(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    // Getters and setters (if needed)
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}