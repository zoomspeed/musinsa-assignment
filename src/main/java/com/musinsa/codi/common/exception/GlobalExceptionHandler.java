package com.musinsa.codi.common.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("Business Exception: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        String message = e.getParams().length > 0 
            ? String.format(errorCode.getMessage(), e.getParams())
            : errorCode.getMessage();
            
        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .message(message)
                .build();
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");
                
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Request format error: {}", e.getMessage());
        
        String errorMessage = createErrorMessageFromCause(e.getCause());
        
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(errorMessage)
                        .build());
    }

    private String createErrorMessageFromCause(Throwable cause) {
        if (cause == null) {
            return "요청 형식이 올바르지 않습니다.";
        }
        
        if (cause instanceof InvalidFormatException) {
            return createInvalidFormatExceptionMessage((InvalidFormatException) cause);
        }
        
        if (cause instanceof JsonParseException) {
            return "JSON 형식이 올바르지 않습니다. 구문 오류를 확인해주세요.";
        }
        
        if (cause instanceof MismatchedInputException) {
            String fieldName = extractFieldName((MismatchedInputException) cause);
            return String.format("'%s' 필드의 타입이 일치하지 않습니다.", fieldName);
        }
        
        if (cause instanceof JsonMappingException) {
            String fieldName = extractFieldName((JsonMappingException) cause);
            return String.format("'%s' 필드를 처리하는 중 오류가 발생했습니다.", fieldName);
        }
        
        return "요청 형식이 올바르지 않습니다.";
    }
    
    private String createInvalidFormatExceptionMessage(InvalidFormatException ife) {
        if (ife.getTargetType() == null) {
            return "요청 형식이 올바르지 않습니다.";
        }
        
        String fieldName = extractFieldName(ife);
        
        if (ife.getTargetType().isEnum()) {
            return String.format("'%s' 필드에 올바르지 않은 값 '%s'가 입력되었습니다. 가능한 값: %s", 
                    fieldName, ife.getValue(), getEnumValues(ife.getTargetType()));
        }
        
        if (Number.class.isAssignableFrom(ife.getTargetType()) || ife.getTargetType().isPrimitive()) {
            return String.format("'%s' 필드는 숫자 형식이어야 합니다. 입력된 값: '%s'", fieldName, ife.getValue());
        }
        
        return String.format("'%s' 필드의 형식이 올바르지 않습니다. 입력된 값: '%s'", fieldName, ife.getValue());
    }

    private String extractFieldName(JsonMappingException exception) {
        List<JsonMappingException.Reference> path = exception.getPath();
        return path.isEmpty() ? "" : path.get(path.size() - 1).getFieldName();
    }

    private String getEnumValues(Class<?> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        // 중복 키 오류인 경우
        if (e.getMessage().contains("PRODUCT_VIEW(ID") && e.getMessage().contains("CATEGORY")) {
            ErrorResponse errorResponse = ErrorResponse.of(
                    HttpStatus.CONFLICT.value(),
                    "이미 동일한 카테고리에 같은 이름의 상품이 존재합니다."
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        
        // 그 외 무결성 제약조건 위반
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "데이터 제약조건 위반이 발생했습니다."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("서버 내부 오류가 발생했습니다.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 