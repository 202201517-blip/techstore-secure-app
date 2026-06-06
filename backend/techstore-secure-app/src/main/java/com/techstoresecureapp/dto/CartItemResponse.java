package com.techstoresecureapp.dto;

import com.techstoresecureapp.entity.CarItem;

import java.math.BigDecimal;

public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productDescription;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private String imageUrl;

    public CartItemResponse(CarItem cartItem) {
        this.id = cartItem.getId();
        this.productId = cartItem.getProduct().getId();
        this.productName = cartItem.getProduct().getName();
        this.productDescription = cartItem.getProduct().getDescription();
        this.unitPrice = cartItem.getProduct().getPrice();
        this.quantity = cartItem.getQuantity();
        this.subtotal = cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        this.imageUrl = cartItem.getProduct().getImageUrl();
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
