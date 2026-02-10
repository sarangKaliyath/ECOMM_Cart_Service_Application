package com.ecomm.ecomm_cart_service_application.services;

import com.ecomm.ecomm_cart_service_application.Repository.CartRepository;
import com.ecomm.ecomm_cart_service_application.constants.CartConstants;
import com.ecomm.ecomm_cart_service_application.dtos.CartDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartItemDto;
import com.ecomm.ecomm_cart_service_application.dtos.CartType;
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


    public Boolean addToCart(String cartId, Long productId, String productName, String imageUrl, Double priceSnapshot, CartType cartType, Integer quantity) {
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

        return true;
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
}
