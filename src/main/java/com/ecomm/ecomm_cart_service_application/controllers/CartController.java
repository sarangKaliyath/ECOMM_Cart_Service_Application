package com.ecomm.ecomm_cart_service_application.controllers;

import com.ecomm.ecomm_cart_service_application.constants.CartConstants;
import com.ecomm.ecomm_cart_service_application.dtos.*;
import com.ecomm.ecomm_cart_service_application.exceptions.CartIdRequiredException;
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
    public ResponseEntity<CartDto> addItemToCart(
            @RequestBody AddToCartRequestDto reqDto,
            @CookieValue(value = "GUEST_CART_ID", required = false) String guestCartId,
            JwtAuthenticationToken token,
            HttpServletResponse response) {

        String cartId;
        CartType cartType = resolveCartType(token);

        if (token != null) {
            cartId = token.getName();
        } else {
            if (guestCartId == null) guestCartId = UUID.randomUUID().toString();
            cartId = guestCartId;
            ResponseCookie cookie = ResponseCookie.from("GUEST_CART_ID", cartId)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(CartConstants.GUEST_CART_TTL)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        CartDto cartDto = cartService.addToCart(cartId, reqDto.getProductId(), reqDto.getProductName(),
                reqDto.getImageUrl(), reqDto.getPriceSnapshot(), cartType, reqDto.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED).body(cartDto);
    }

    @PatchMapping("/quantity")
    public ResponseEntity<CartItemResponseDto> updateCartItemQuantity(
            @RequestBody CartUpdateQuantityRequestDto reqDto,
            @CookieValue(value = "GUEST_CART_ID", required = false) String guestCartId,
            JwtAuthenticationToken token) {

        String cartId = resolveCartId(token, guestCartId);
        CartType cartType = resolveCartType(token);

        CartItemDto cartItem = cartService.updateCartItemQuantity(cartId, reqDto.getProductId(), cartType, reqDto.getQuantity());
        return ResponseEntity.status(HttpStatus.OK).body(CartMapper.toCartItemResponseDto(cartItem));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<HttpStatus> removeItemFromCart(
            @PathVariable Long productId,
            @CookieValue(value = "GUEST_CART_ID", required = false) String guestCartId,
            JwtAuthenticationToken token) {

        String cartId = resolveCartId(token, guestCartId);
        CartType cartType = resolveCartType(token);

        return cartService.removeFromCart(cartType, cartId, productId)
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<HttpStatus> clearCart(
            @CookieValue(value = "GUEST_CART_ID", required = false) String guestCartId,
            JwtAuthenticationToken token) {

        String cartId = resolveCartId(token, guestCartId);
        CartType cartType = resolveCartType(token);

        return cartService.clearCart(cartType, cartId)
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/get")
    public ResponseEntity<CartResponseDto> getCartItems(
            @CookieValue(value = "GUEST_CART_ID", required = false) String guestCartId,
            JwtAuthenticationToken token,
            HttpServletResponse response) {

        String cartId;
        CartType cartType = resolveCartType(token);

        if (token != null) {
            cartId = token.getName();
        } else {
            if (guestCartId == null) {
                guestCartId = UUID.randomUUID().toString();
                ResponseCookie cookie = ResponseCookie.from("GUEST_CART_ID", guestCartId)
                        .httpOnly(true)
                        .path("/")
                        .maxAge(CartConstants.GUEST_CART_TTL)
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            }
            cartId = guestCartId;
        }

        CartDto cart = cartService.getCart(cartId, cartType);
        return ResponseEntity.status(HttpStatus.OK).body(CartMapper.toCartResponseDto(cart));
    }

    @PostMapping("/merge")
    public ResponseEntity<CartDto> mergeCarts(
            @CookieValue(value = "GUEST_CART_ID", required = false) String guestCartId,
            JwtAuthenticationToken token,
            HttpServletResponse response) {

        String userId = token.getName();

        CartDto mergedCart = cartService.mergeCart(guestCartId, userId);

        ResponseCookie clearCookie = ResponseCookie.from("GUEST_CART_ID", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResponseEntity.status(HttpStatus.OK).body(mergedCart);
    }

    private CartType resolveCartType(JwtAuthenticationToken token) {
        return token != null ? CartType.USER : CartType.GUEST;
    }

    private String resolveCartId(JwtAuthenticationToken token, String guestCartId) {
        if (token != null) return token.getName();
        if (guestCartId == null || guestCartId.isBlank())
            throw new CartIdRequiredException("GUEST_CART_ID is required");
        return guestCartId;
    }
}
