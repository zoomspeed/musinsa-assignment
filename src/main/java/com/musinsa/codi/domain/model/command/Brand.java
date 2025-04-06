package com.musinsa.codi.domain.model.command;

import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @Builder
    public Brand(String name) {
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void addProduct(Product product) {
        validateProductCategory(product);
        validateProductId(product);
        product.setBrand(this);
        products.add(product);
    }

    private void validateProductCategory(Product product) {
        if (products.stream().anyMatch(p -> p.getCategory() == product.getCategory())) {
            throw new BusinessException(ErrorCode.PRODUCT_CATEGORY_ALREADY_EXISTS);
        }
    }

    private void validateProductId(Product product) {
        if (product.getId() != null && products.stream().anyMatch(p -> p.getId().equals(product.getId()))) {
            throw new BusinessException(ErrorCode.PRODUCT_ALREADY_EXISTS);
        }
    }

    public Product findProductById(Long productId) {
        return products.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public Product findProductByName(String productName) {
        return products.stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public void removeProduct(Long productId) {
        Product product = findProductById(productId);
        products.remove(product);
    }

    public void updateProduct(Long productId, Product updatedProduct) {
        Product existingProduct = findProductById(productId);
        validateProductCategory(updatedProduct);
        // 다른 상품이 이미 해당 카테고리를 사용하고 있는지 확인
        if (products.stream()
                .filter(p -> !p.getId().equals(productId))
                .anyMatch(p -> p.getCategory() == updatedProduct.getCategory())) {
            throw new BusinessException(ErrorCode.PRODUCT_CATEGORY_ALREADY_EXISTS);
        }

        existingProduct.update(updatedProduct.getName(), updatedProduct.getPrice(), updatedProduct.getCategory());
    }
} 