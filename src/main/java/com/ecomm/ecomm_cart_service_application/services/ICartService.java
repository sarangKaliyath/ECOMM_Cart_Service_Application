package com.ecomm.ecomm_cart_service_application.services;

import com.ecomm.ecomm_cart_service_application.dtos.CartDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartItemDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartType;

public interface ICartService {

    CartDto addToCart(String cartId, Long productId, String productName, String productImageUrl, Double priceSnapshot, CartType cartType, Integer quantity);

    CartItemDto updateCartItemQuantity(String cartId, Long productId, Integer quantity);

    Boolean removeFromCart(CartType cartType, String cartId, Long productId);

    Boolean clearCart(String cartId);

    CartDto getCart(String cartId, CartType cartType);

    CartDto mergeCart(String guestCartId, String userCartId);
}
