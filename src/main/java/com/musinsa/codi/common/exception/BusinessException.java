package com.musinsa.codi.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] params;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.params = new Object[]{};
    }

    public BusinessException(ErrorCode errorCode, Object... params) {
        super(String.format(errorCode.getMessage(), params));
        this.errorCode = errorCode;
        this.params = params;
    }
} 