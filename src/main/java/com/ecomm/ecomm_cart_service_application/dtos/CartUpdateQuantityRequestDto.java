package com.ecomm.ecomm_cart_service_application.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartUpdateQuantityRequestDto {
    private Long productId;
    private Integer quantity;
}
