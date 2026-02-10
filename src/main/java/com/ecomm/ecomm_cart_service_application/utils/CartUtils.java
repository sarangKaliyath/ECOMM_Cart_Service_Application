package com.ecomm.ecomm_cart_service_application.utils;

import com.ecomm.ecomm_cart_service_application.dtos.CartDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartItemDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartType;

import java.util.ArrayList;
import java.util.Date;

public class CartUtils {

    public static CartDto createNewCart(String cartId, CartType cartType) {
        CartDto cart = new CartDto();
        cart.setCartId(cartId);
        cart.setCartType(cartType);
        cart.setCartItems(new ArrayList<>());
        cart.setTotalPrice(0.0);
        cart.setTotalQuantity(0);
        cart.setLastUpdatedAt(new Date());
        return cart;
    }

    public static void recalculateCart(CartDto cart) {
        int totalQuantity = 0;
        double totalPrice = 0.0;

        for (CartItemDto cartItem : cart.getCartItems()) {
            totalQuantity += cartItem.getQuantity();
            if (cartItem.getPriceSnapshot() != null) {
                totalPrice += cartItem.getQuantity() * cartItem.getPriceSnapshot();
            }

            cart.setTotalQuantity(totalQuantity);
            cart.setTotalPrice(totalPrice);
        }
    }
}
