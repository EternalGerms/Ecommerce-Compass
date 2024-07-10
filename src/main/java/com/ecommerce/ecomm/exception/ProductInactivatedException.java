package com.ecommerce.ecomm.exception;

public class ProductInactivatedException extends RuntimeException {
    public ProductInactivatedException(String message) {
        super(message);
    }
}