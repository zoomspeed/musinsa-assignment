package com.musinsa.codi.adapter.outbound.persistence.adapter.query;

import com.musinsa.codi.domain.model.query.CodiView;
import com.musinsa.codi.domain.port.query.CodiQueryPort;
import com.musinsa.codi.adapter.outbound.persistence.repository.query.CodiQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CodiQueryPersistenceAdapter implements CodiQueryPort {
    private final CodiQueryRepository codiViewRepository;

    @Override
    public List<CodiView> findAll() {
        return codiViewRepository.findAll();
    }

    @Override
    public Optional<CodiView> findById(Long id) {
        return codiViewRepository.findById(id);
    }
} 