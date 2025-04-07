package com.musinsa.codi.domain.service.query;

import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.domain.model.query.CodiView;
import com.musinsa.codi.domain.port.query.CodiQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodiQueryService {
    private final CodiQueryPort codiQueryPort;

    public List<CodiView> getAllCodis() {
        return codiQueryPort.findAll();
    }

    public CodiView getCodiById(Long id) {
        return codiQueryPort.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CODI_NOT_FOUND));
    }
} 