package com.musinsa.codi.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "브랜드를 찾을 수 없습니다."),
    BRAND_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 브랜드입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 상품입니다."),
    CODI_NOT_FOUND(HttpStatus.NOT_FOUND, "코디를 찾을 수 없습니다."),
    INVALID_PRICE(HttpStatus.BAD_REQUEST, "유효하지 않은 가격입니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
} 