package com.ecomm.ecomm_cart_service_application.services;

import com.ecomm.ecomm_cart_service_application.dtos.CartDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartType;

public interface ICartService {

    Boolean addToCart(String cartId, Long productId, String productName, String productImageUrl, Double priceSnapshot, CartType cartType, Integer quantity);

    Boolean removeFromCart(String cartId, Long productId);

    Boolean clearCart(String cartId);

    CartDto getCart(String cartId, CartType cartType);
}
