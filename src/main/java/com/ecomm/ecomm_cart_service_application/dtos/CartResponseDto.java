package com.ecomm.ecomm_cart_service_application.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CartResponseDto implements Serializable {
//    private String cartId;
    private CartType cartType;
    private List<CartItemDto> cartItems;
    private Double totalPrice;
    private Integer totalQuantity;
    private Date lastUpdatedAt;
}