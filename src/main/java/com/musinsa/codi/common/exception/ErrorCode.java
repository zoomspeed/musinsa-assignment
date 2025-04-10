package com.musinsa.codi.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Brand related
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "브랜드를 찾을 수 없습니다: %s"),
    BRAND_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 브랜드입니다: %s"),
    BRAND_NO_PRODUCTS(HttpStatus.NOT_FOUND, "해당 브랜드에 상품이 없습니다: %s"),
    BRAND_HAS_PRODUCTS(HttpStatus.CONFLICT, "해당 브랜드에 이미 상품이 존재하여 브랜드를 삭제할 수 없습니다. %s"),
    
    // Product related
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다: %s"),
    PRODUCT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 상품입니다: %s"),
    PRODUCT_CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 상품 카테고리입니다: %s"),
    
    // Category related
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다: %s"),
    CATEGORY_NO_PRODUCTS(HttpStatus.NOT_FOUND, "해당 카테고리에 상품이 없습니다: %s"),
    CATEGORY_LOWEST_PRICE_NOT_FOUND(HttpStatus.NOT_FOUND, "최저가 상품을 찾을 수 없습니다."),
    CATEGORY_HIGHEST_PRICE_NOT_FOUND(HttpStatus.NOT_FOUND, "최고가 상품을 찾을 수 없습니다."),
    
    // Common
    INVALID_PRICE(HttpStatus.BAD_REQUEST, "유효하지 않은 가격입니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
} 