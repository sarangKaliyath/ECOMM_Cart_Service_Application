package com.ecomm.ecomm_cart_service_application.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CartItemDto implements Serializable {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double priceSnapshot;
    private String imageUrl;
}
