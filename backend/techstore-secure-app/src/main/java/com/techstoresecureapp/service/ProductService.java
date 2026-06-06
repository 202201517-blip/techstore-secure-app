package com.techstoresecureapp.service;

import com.techstoresecureapp.dto.CategoryResponse;
import com.techstoresecureapp.dto.ProductRequest;
import com.techstoresecureapp.dto.ProductResponse;
import com.techstoresecureapp.entity.Category;
import com.techstoresecureapp.entity.Product;
import com.techstoresecureapp.repository.CategoryRepository;
import com.techstoresecureapp.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByActiveTrue()
                .stream()
                .map(CategoryResponse::new)
                .toList();
    }

    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(ProductResponse::new)
                .toList();
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId)
                .stream()
                .map(ProductResponse::new)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return new ProductResponse(product);
    }

    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Product product = new Product(
                category,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock(),
                request.getImageUrl()
        );

        Product savedProduct = productRepository.save(product);

        return new ProductResponse(savedProduct);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        product.setCategory(category);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());

        Product updatedProduct = productRepository.save(product);

        return new ProductResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        product.setActive(false);

        productRepository.save(product);
    }
}
