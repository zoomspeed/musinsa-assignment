package com.musinsa.codi.adapter.outbound.persistence.repository.command;

import com.musinsa.codi.domain.model.command.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCommandRepository extends JpaRepository<Product, Long> {
} 