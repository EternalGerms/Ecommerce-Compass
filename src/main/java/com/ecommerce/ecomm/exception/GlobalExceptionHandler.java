package com.ecommerce.ecomm.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("IllegalArgumentException caught: ", ex);
        ErrorResponse error = new ErrorResponse(400, "Bad Request", ex.getMessage());
        logger.info("Returning error response: {}", error);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        logger.error("Exception caught: ", ex);
        ErrorResponse error = new ErrorResponse(500, "Internal Server Error", "Ocorreu um erro inesperado");
        logger.info("Returning error response: {}", error);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

class ErrorResponse {
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