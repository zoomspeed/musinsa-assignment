package com.musinsa.codi.adapter.outbound.persistence.repository.command;

import com.musinsa.codi.domain.model.command.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryCommandRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCode(String code);
    boolean existsByCode(String code);
} 