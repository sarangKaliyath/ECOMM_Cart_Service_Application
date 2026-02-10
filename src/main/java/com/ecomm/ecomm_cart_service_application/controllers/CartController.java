package com.ecomm.ecomm_cart_service_application.controllers;

import com.ecomm.ecomm_cart_service_application.Repository.CartRepository;
import com.ecomm.ecomm_cart_service_application.constants.CartConstants;
import com.ecomm.ecomm_cart_service_application.dtos.AddToCartRequestDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartItemDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartType;
import com.ecomm.ecomm_cart_service_application.exceptions.InvalidQuantityException;
import com.ecomm.ecomm_cart_service_application.services.ICartService;
import com.ecomm.ecomm_cart_service_application.utils.CartKeyUtil;
import com.ecomm.ecomm_cart_service_application.utils.CartUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService cartService;


    @PostMapping("/add")
    public ResponseEntity<Boolean> addItemToCart(@RequestBody AddToCartRequestDto reqDto) {
        cartService.addToCart(reqDto.getCartId(), reqDto.getProductId(), reqDto.getProductName(), reqDto.getImageUrl(), reqDto.getPriceSnapshot(), reqDto.getCartType(), reqDto.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    public void removeItemFromCart() {

    }

    public void clearCart() {

    }

    @GetMapping("/get/{cartId}/{cartType}")
    public ResponseEntity<CartDto> getCartItems(@PathVariable String cartId, @PathVariable CartType cartType) {
        CartDto cart = cartService.getCart(cartId, cartType);
        return ResponseEntity.status(HttpStatus.OK).body(cart);
    }

}
