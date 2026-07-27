package com.ecomm.ecomm_cart_service_application.mapper;

import com.ecomm.ecomm_cart_service_application.dtos.*;

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

    public static CartItemResponseDto toCartItemResponseDto(CartItemDto cartItemDto) {
        if (cartItemDto == null) {
            return null;
        }

        CartItemResponseDto response = new CartItemResponseDto();
        response.setProductId(cartItemDto.getProductId());
        response.setProductName(cartItemDto.getProductName());
        response.setImageUrl(cartItemDto.getImageUrl());
        response.setQuantity(cartItemDto.getQuantity());
        response.setPriceSnapshot(cartItemDto.getPriceSnapshot());

        return response;
    }


    public CartDto toResponse(Object cart, CartType cartType) {
        return null;
    }

    public CartDto toEntity(Object cart) {
        return null;
    }
}