package com.ecomm.ecomm_cart_service_application.constants;

import java.time.Duration;

public final class CartConstants {
    public static final String CART_KEY_PREFIX = "CART";
    public static final Duration GUEST_CART_TTL = Duration.ofMinutes(7);
    public static final Duration USER_CART_TTL = Duration.ofMinutes(30);
}
