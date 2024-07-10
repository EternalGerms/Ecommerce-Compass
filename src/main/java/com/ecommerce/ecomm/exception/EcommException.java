package com.ecommerce.ecomm.exception;

public class EcommException extends RuntimeException {
    private final ErrorCode errorCode;

    public EcommException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}