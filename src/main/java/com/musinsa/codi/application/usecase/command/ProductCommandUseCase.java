package com.musinsa.codi.application.usecase.command;

import com.musinsa.codi.common.dto.command.ProductCommandRequest;
import com.musinsa.codi.domain.model.command.Product;

public interface ProductCommandUseCase {
    Product addProduct(String brandName, ProductCommandRequest request);
    Product updateProduct(String brandName, Long productId, ProductCommandRequest request);
    void deleteProduct(String brandName, Long productId);
} 