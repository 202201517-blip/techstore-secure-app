package com.techstoresecureapp.dto;

import com.techstoresecureapp.entity.Product;

import java.math.BigDecimal;

public class ProductResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Boolean active;

    public ProductResponse(Product product) {
        this.id = product.getId();

        if (product.getCategory() != null) {
            this.categoryId = product.getCategory().getId();
            this.categoryName = product.getCategory().getName();
        }

        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.imageUrl = product.getImageUrl();
        this.active = product.getActive();
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean getActive() {
        return active;
    }
}
