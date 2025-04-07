package com.musinsa.codi.adapter.outbound.persistence.adapter.command;

import com.musinsa.codi.adapter.outbound.persistence.repository.command.CategoryCommandRepository;
import com.musinsa.codi.domain.model.command.Category;
import com.musinsa.codi.domain.port.command.CategoryCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryCommandPersistenceAdapter implements CategoryCommandPort {
    private final CategoryCommandRepository categoryRepository;

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<Category> findByCode(String code) {
        return categoryRepository.findByCode(code);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return categoryRepository.existsByCode(code);
    }
} 