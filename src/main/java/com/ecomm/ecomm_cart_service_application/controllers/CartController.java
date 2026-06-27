package com.ecomm.ecomm_cart_service_application.controllers;

import com.ecomm.ecomm_cart_service_application.constants.CartConstants;
import com.ecomm.ecomm_cart_service_application.dtos.AddToCartRequestDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartResponseDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartType;
import com.ecomm.ecomm_cart_service_application.mapper.CartMapper;
import com.ecomm.ecomm_cart_service_application.services.ICartService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService cartService;


    @PostMapping("/add")
    public ResponseEntity<CartDto> addItemToCart(@RequestBody AddToCartRequestDto reqDto, @CookieValue(value = "GUEST_CART_ID", required = false)
    String guestCartId, HttpServletResponse response) {
        if (guestCartId == null) guestCartId = UUID.randomUUID().toString();

        ResponseCookie cookie = ResponseCookie.from("GUEST_CART_ID", guestCartId)
                .httpOnly(true)
                .path("/")
                .maxAge(CartConstants.GUEST_CART_TTL)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        CartDto cartDto = cartService.addToCart(guestCartId, reqDto.getProductId(), reqDto.getProductName(), reqDto.getImageUrl(), reqDto.getPriceSnapshot(), reqDto.getCartType(), reqDto.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED).body(cartDto);
    }

    public void removeItemFromCart() {

    }

    public void clearCart() {

    }

    @GetMapping("/get/{cartType}")
    public ResponseEntity<CartResponseDto> getCartItems(
            @PathVariable CartType cartType,
            @CookieValue(value = "GUEST_CART_ID", required = false) String cartId) {

        if (cartId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        CartDto cart = cartService.getCart(cartId, cartType);

        CartResponseDto cartResponse = CartMapper.toCartResponseDto(cart);
        return ResponseEntity.status(HttpStatus.OK).body(cartResponse);
    }

    @PostMapping("/merge")
    public ResponseEntity<CartDto> mergeCarts(@CookieValue(value = "GUEST_CART_ID", required = false) String guestCartId, JwtAuthenticationToken token) {
        System.out.println(token);
        String userId = token.getName();

        System.out.println(userId);

        if (guestCartId == null) {
            return ResponseEntity.status(HttpStatus.OK).body(cartService.getCart(userId, CartType.USER));
        }

        return ResponseEntity.status(HttpStatus.OK).body(cartService.mergeCart(guestCartId, userId));
    }
}