package com.musinsa.codi.adapter.outbound.persistence.repository.query;

import com.musinsa.codi.domain.model.query.CodiView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodiQueryRepository extends JpaRepository<CodiView, Long> {
} 