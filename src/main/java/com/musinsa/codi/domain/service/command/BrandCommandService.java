package com.musinsa.codi.domain.service.command;

import com.musinsa.codi.application.usecase.command.BrandCommandUseCase;
import com.musinsa.codi.common.annotation.PublishBrandEvent;
import com.musinsa.codi.common.dto.command.BrandCommandRequest;
import com.musinsa.codi.common.exception.BusinessException;
import com.musinsa.codi.common.exception.ErrorCode;
import com.musinsa.codi.common.util.MessageUtils;
import com.musinsa.codi.domain.event.BrandEventType;
import com.musinsa.codi.domain.model.command.Brand;
import com.musinsa.codi.domain.port.command.BrandCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandCommandService implements BrandCommandUseCase {
    private final BrandCommandPort brandCommandPort;
    private final MessageUtils messageUtils;

    @Override
    @PublishBrandEvent(eventType = BrandEventType.CREATED)
    public Brand createBrand(BrandCommandRequest request) {
        validateBrandNameNotExists(request.getName());
        Brand brand = Brand.builder()
                .name(request.getName())
                .build();
        return brandCommandPort.save(brand);
    }

    @Override
    @PublishBrandEvent(eventType = BrandEventType.UPDATED)
    public Brand updateBrand(String brandName, BrandCommandRequest request) {
        Brand brand = findBrandByName(brandName);
        validateBrandNameNotExists(request.getName());
        brand.updateName(request.getName());
        return brandCommandPort.save(brand);
    }

    @Override
    @PublishBrandEvent(eventType = BrandEventType.DELETED)
    public Brand deleteBrand(String brandName) {
        Brand brand = findBrandByName(brandName);
        brandCommandPort.delete(brand.getId());
        return brand;
    }

    private Brand findBrandByName(String name) {
        return brandCommandPort.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND, 
                    messageUtils.getMessage("error.brand.not.found", name)));
    }

    private void validateBrandNameNotExists(String name) {
        if (brandCommandPort.findByName(name).isPresent()) {
            throw new BusinessException(ErrorCode.BRAND_ALREADY_EXISTS, 
                messageUtils.getMessage("error.brand.name.already.exists", name));
        }
    }
} 