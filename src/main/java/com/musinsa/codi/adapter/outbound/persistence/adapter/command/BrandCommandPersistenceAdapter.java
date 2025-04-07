package com.musinsa.codi.adapter.outbound.persistence.adapter.command;

import com.musinsa.codi.domain.model.command.Brand;
import com.musinsa.codi.domain.port.command.BrandCommandPort;
import com.musinsa.codi.adapter.outbound.persistence.repository.command.BrandCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BrandCommandPersistenceAdapter implements BrandCommandPort {
    private final BrandCommandRepository brandRepository;

    @Override
    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    public void delete(Long id) {
        brandRepository.deleteById(id);
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    public Optional<Brand> findByName(String name) {
        return brandRepository.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {
        return brandRepository.existsByName(name);
    }
} 