package com.techstoresecureapp.service;

import com.techstoresecureapp.dto.CartItemRequest;
import com.techstoresecureapp.dto.CartItemResponse;
import com.techstoresecureapp.dto.CartSummaryResponse;
import com.techstoresecureapp.entity.AppUser;
import com.techstoresecureapp.entity.CarItem;
import com.techstoresecureapp.entity.Product;
import com.techstoresecureapp.repository.CartItemRepository;
import com.techstoresecureapp.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartItemRepository cartItemRepository,
            ProductRepository productRepository
    ) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public CartSummaryResponse getCart(AppUser user) {
        List<CartItemResponse> items = cartItemRepository.findByUserId(user.getId())
                .stream()
                .map(CartItemResponse::new)
                .toList();

        return new CartSummaryResponse(items);
    }

    public CartSummaryResponse addItem(AppUser user, CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!product.getActive()) {
            throw new RuntimeException("El producto no está disponible");
        }

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("No hay suficiente stock disponible");
        }

        CarItem cartItem = cartItemRepository
                .findByUserIdAndProductId(user.getId(), product.getId())
                .orElse(null);

        if (cartItem == null) {
            cartItem = new CarItem(
                    user,
                    product,
                    request.getQuantity()
            );
        } else {
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (product.getStock() < newQuantity) {
                throw new RuntimeException("No hay suficiente stock disponible");
            }

            cartItem.setQuantity(newQuantity);
        }

        cartItemRepository.save(cartItem);

        return getCart(user);
    }

    public CartSummaryResponse updateItem(AppUser user, Long itemId, CartItemRequest request) {
        CarItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Producto del carrito no encontrado"));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No puedes modificar este carrito");
        }

        Product product = cartItem.getProduct();

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("No hay suficiente stock disponible");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return getCart(user);
    }

    public CartSummaryResponse removeItem(AppUser user, Long itemId) {
        CarItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Producto del carrito no encontrado"));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No puedes eliminar este producto del carrito");
        }

        cartItemRepository.delete(cartItem);

        return getCart(user);
    }

    @Transactional
    public CartSummaryResponse clearCart(AppUser user) {
        cartItemRepository.deleteByUserId(user.getId());

        return getCart(user);
    }
}
