package com.ecommerce.ecomm.model;

public class ErrorResponse {
    private int code;
    private String status;
    private String message;

    public ErrorResponse(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    // Getters e Setters

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "code=" + code +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}