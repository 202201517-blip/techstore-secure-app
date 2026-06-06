package com.techstoresecureapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CarItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Usuario dueño del carrito.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    /*
     * Producto agregado al carrito.
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public CarItem() {
    }

    public CarItem(AppUser user, Product product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
