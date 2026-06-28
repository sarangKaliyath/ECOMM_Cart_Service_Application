package com.ecomm.ecomm_cart_service_application.exceptions;

public class CartIdRequiredException extends RuntimeException {
    public CartIdRequiredException(String message) {
        super(message);
    }
}
