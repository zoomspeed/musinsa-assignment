package com.musinsa.codi.domain.port.query;

import com.musinsa.codi.domain.model.query.CodiView;
import java.util.List;
import java.util.Optional;

public interface CodiQueryPort {
    List<CodiView> findAll();
    Optional<CodiView> findById(Long id);
} 