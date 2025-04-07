package com.musinsa.codi.adapter.outbound.persistence.adapter.command;

import com.musinsa.codi.domain.model.command.Product;
import com.musinsa.codi.domain.port.command.ProductCommandPort;
import com.musinsa.codi.adapter.outbound.persistence.repository.command.ProductCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCommandPersistenceAdapter implements ProductCommandPort {
    private final ProductCommandRepository productRepository;

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
} 