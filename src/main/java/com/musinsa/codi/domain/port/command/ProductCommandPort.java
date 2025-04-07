package com.musinsa.codi.domain.port.command;

import com.musinsa.codi.domain.model.command.Product;
import java.util.Optional;

public interface ProductCommandPort {
    Product save(Product product);
    void delete(Long id);
    Optional<Product> findById(Long id);
} 