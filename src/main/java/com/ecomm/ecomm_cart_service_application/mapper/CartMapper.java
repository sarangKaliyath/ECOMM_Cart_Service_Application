package com.ecomm.ecomm_cart_service_application.mapper;

import com.ecomm.ecomm_cart_service_application.dtos.CartDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartItemDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartResponseDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartType;

public class CartMapper {

    // Mapping method from CartDto to CartResponseDto
    public static CartResponseDto toCartResponseDto(CartDto cartDto) {
        if (cartDto == null) {
            return null;
        }

        CartResponseDto response = new CartResponseDto();
//        response.setCartId(cartDto.getCartId());
        response.setTotalPrice(cartDto.getTotalPrice());
        response.setTotalQuantity(cartDto.getTotalQuantity());
        response.setLastUpdatedAt(cartDto.getLastUpdatedAt());
        response.setCartType(cartDto.getCartType());

        // Map each CartItemDto directly to response's list
        response.setCartItems(cartDto.getCartItems());

        return response;
    }

    public CartDto toResponse(Object cart, CartType cartType) {
        return null;
    }

    public CartDto toEntity(Object cart) {
        return null;
    }
}