package com.techstoresecureapp.controller;

import com.techstoresecureapp.dto.CartItemRequest;
import com.techstoresecureapp.dto.CartSummaryResponse;
import com.techstoresecureapp.entity.AppUser;
import com.techstoresecureapp.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartSummaryResponse getCart(
            @AuthenticationPrincipal AppUser user
    ) {
        return cartService.getCart(user);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartSummaryResponse addItem(
            @AuthenticationPrincipal AppUser user,
            @Valid @RequestBody CartItemRequest request
    ) {
        return cartService.addItem(user, request);
    }

    @PutMapping("/items/{itemId}")
    public CartSummaryResponse updateItem(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemRequest request
    ) {
        return cartService.updateItem(user, itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CartSummaryResponse removeItem(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long itemId
    ) {
        return cartService.removeItem(user, itemId);
    }

    @DeleteMapping
    public CartSummaryResponse clearCart(
            @AuthenticationPrincipal AppUser user
    ) {
        return cartService.clearCart(user);
    }
}
