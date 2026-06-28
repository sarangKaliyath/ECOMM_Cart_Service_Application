package com.ecomm.ecomm_cart_service_application.services;

import com.ecomm.ecomm_cart_service_application.Repository.CartRepository;
import com.ecomm.ecomm_cart_service_application.constants.CartConstants;
import com.ecomm.ecomm_cart_service_application.dtos.CartDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartItemDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartType;
import com.ecomm.ecomm_cart_service_application.exceptions.CartIdRequiredException;
import com.ecomm.ecomm_cart_service_application.exceptions.InvalidQuantityException;
import com.ecomm.ecomm_cart_service_application.utils.CartKeyUtil;
import com.ecomm.ecomm_cart_service_application.utils.CartUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CartService implements ICartService {

    @Autowired
    private CartRepository cartRepository;


    public CartDto addToCart(String cartId, Long productId, String productName, String imageUrl, Double priceSnapshot, CartType cartType, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException("Invalid quantity");
        }

        String redisKey = CartKeyUtil.cartKey(cartType, cartId);

        CartDto cart = cartRepository.get(redisKey);

        if (cart == null) {
            cart = CartUtils.createNewCart(cartId, cartType);
        }

        boolean itemFound = false;

        for (CartItemDto item : cart.getCartItems()) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                itemFound = true;
                break;
            }
        }

        if (!itemFound) {
            CartItemDto newCartItem = new CartItemDto();
            newCartItem.setProductId(productId);
            newCartItem.setQuantity(quantity);
            newCartItem.setProductName(productName);
            newCartItem.setPriceSnapshot(priceSnapshot);
            newCartItem.setImageUrl(imageUrl);

            cart.getCartItems().add(newCartItem);
        }

        CartUtils.recalculateCart(cart);
        cart.setLastUpdatedAt(new Date());

        cartRepository.save(redisKey, cart, cartType == CartType.GUEST ? CartConstants.GUEST_CART_TTL : CartConstants.USER_CART_TTL);

        return cart;
    }

    public CartDto updateCartItemQuantity(String cartId, Long productId, Integer quantity) {
        if(productId == null) throw new IllegalArgumentException("Product ID cannot be null");
        if(quantity == null || quantity <= 0) throw new InvalidQuantityException("Invalid quantity");

        String redisKey = CartKeyUtil.cartKey(CartType.GUEST, cartId);

        CartDto cart = cartRepository.get(redisKey);

        if(cart == null) throw new CartIdRequiredException("Cart not found");

        for(CartItemDto item : cart.getCartItems()) {
            if(item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                CartUtils.recalculateCart(cart);
                cart.setLastUpdatedAt(new Date());
                cartRepository.save(redisKey, cart, CartConstants.GUEST_CART_TTL);
            }
        }

        cartRepository.save(redisKey, cart, CartConstants.GUEST_CART_TTL);

        return cart;
    }

    public Boolean removeFromCart(String cartId, Long productId) {
        return false;
    }

    public Boolean clearCart(String cartId) {
        return false;
    }

    public CartDto getCart(String cartId, CartType cartType) {
        if (cartId.isBlank()) throw new IllegalArgumentException("Cart ID cannot be blank");

        String redisKey = CartKeyUtil.cartKey(cartType, cartId);

        return cartRepository.get(redisKey);
    }

    public CartDto mergeCart(String guestCartId, String userCartId) {
        if (guestCartId == null || guestCartId.isBlank()) {
            throw new IllegalArgumentException("Guest cart ID cannot be blank");
        }
        if (userCartId == null || userCartId.isBlank()) {
            throw new IllegalArgumentException("User cart ID cannot be blank");
        }

        // Get the guest cart
        String guestRedisKey = CartKeyUtil.cartKey(CartType.GUEST, guestCartId);
        CartDto guestCart = cartRepository.get(guestRedisKey);

        // If guest cart doesn't exist or is empty, get or create user cart
        if (guestCart == null || guestCart.getCartItems().isEmpty()) {
            CartDto userCart = getCart(userCartId, CartType.USER);
            if (userCart == null) {
                userCart = CartUtils.createNewCart(userCartId, CartType.USER);
                String userRedisKey = CartKeyUtil.cartKey(CartType.USER, userCartId);
                cartRepository.save(userRedisKey, userCart, CartConstants.USER_CART_TTL);
            }
            return userCart;
        }

        // Get or create user cart
        String userRedisKey = CartKeyUtil.cartKey(CartType.USER, userCartId);
        CartDto userCart = cartRepository.get(userRedisKey);

        if (userCart == null) {
            userCart = CartUtils.createNewCart(userCartId, CartType.USER);
        }

        // Merge guest cart items into user cart
        for (CartItemDto guestItem : guestCart.getCartItems()) {
            boolean itemFound = false;

            // Check if item already exists in user cart
            for (CartItemDto userItem : userCart.getCartItems()) {
                if (userItem.getProductId().equals(guestItem.getProductId())) {
                    // Combine quantities
                    userItem.setQuantity(userItem.getQuantity() + guestItem.getQuantity());
                    itemFound = true;
                    break;
                }
            }

            // If item doesn't exist in user cart, add it
            if (!itemFound) {
                CartItemDto newItem = new CartItemDto();
                newItem.setProductId(guestItem.getProductId());
                newItem.setQuantity(guestItem.getQuantity());
                newItem.setProductName(guestItem.getProductName());
                newItem.setPriceSnapshot(guestItem.getPriceSnapshot());
                newItem.setImageUrl(guestItem.getImageUrl());

                userCart.getCartItems().add(newItem);
            }
        }

        // Recalculate totals and update timestamp
        CartUtils.recalculateCart(userCart);
        userCart.setLastUpdatedAt(new Date());

        // Save the merged user cart
        cartRepository.save(userRedisKey, userCart, CartConstants.USER_CART_TTL);

        // Clean up guest cart
        cartRepository.delete(guestRedisKey);

        return userCart;
    }
}