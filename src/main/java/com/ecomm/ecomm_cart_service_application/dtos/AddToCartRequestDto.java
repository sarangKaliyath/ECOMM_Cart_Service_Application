package com.ecomm.ecomm_cart_service_application.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequestDto {
    private Long productId;
    private Integer quantity;
    private String imageUrl;
    private String productName;
    private Double priceSnapshot;
}
