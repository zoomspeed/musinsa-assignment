package com.musinsa.codi.domain.port.command;

import com.musinsa.codi.domain.model.command.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryCommandPort {
    Category save(Category category);
    Optional<Category> findById(Long id);
    Optional<Category> findByCode(String code);
    List<Category> findAll();
    void delete(Long id);
    boolean existsByCode(String code);
} 