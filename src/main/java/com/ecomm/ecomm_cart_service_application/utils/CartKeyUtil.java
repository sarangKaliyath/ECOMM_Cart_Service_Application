package com.ecomm.ecomm_cart_service_application.utils;

import com.ecomm.ecomm_cart_service_application.constants.CartConstants;
import com.ecomm.ecomm_cart_service_application.dtos.CartType;

public class CartKeyUtil {
    public static String cartKey(CartType cartType, String cartId) {
        return CartConstants.CART_KEY_PREFIX + ":" + cartType.name().toUpperCase() + ":" + cartId;
    }
}
