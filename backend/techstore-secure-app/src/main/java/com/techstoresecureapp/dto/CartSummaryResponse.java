package com.techstoresecureapp.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartSummaryResponse {

    private List<CartItemResponse> items;
    private Integer totalItems;
    private BigDecimal totalAmount;

    public CartSummaryResponse(List<CartItemResponse> items) {
        this.items = items;
        this.totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        this.totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}