package com.techstoresecureapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartItemRequest {

    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}