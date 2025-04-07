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
    
    @Transient  // DB에 저장되지 않는 임시 필드
    private Product lastAddedProduct;

    @Builder
    public Brand(String name) {
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void addProduct(Product product) {
        validateProductId(product);
        product.setBrand(this);
        products.add(product);
        this.lastAddedProduct = product;  // 마지막으로 추가된 상품 추적
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
    
    public Product getLastAddedProduct() {
        if (lastAddedProduct == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return lastAddedProduct;
    }
} 