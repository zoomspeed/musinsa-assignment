package com.musinsa.codi.domain.port.command;

import com.musinsa.codi.domain.model.command.Brand;
import java.util.Optional;

public interface BrandCommandPort {
    Brand save(Brand brand);
    void delete(Long id);
    Optional<Brand> findById(Long id);
    Optional<Brand> findByName(String name);
    boolean existsByName(String name);
} 