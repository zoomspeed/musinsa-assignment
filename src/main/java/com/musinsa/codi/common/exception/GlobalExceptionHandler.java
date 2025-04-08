package com.musinsa.codi.common.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.musinsa.codi.common.util.MessageUtils;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageUtils messageUtils;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("Business Exception: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        String message = e.getMessage();
            
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
                .orElse(messageUtils.getMessage("error.bad.request"));
                
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
            return getDefaultFormatErrorMessage();
        }

        if (cause instanceof InvalidFormatException ife) {
            return createInvalidFormatExceptionMessage(ife);
        } else if (cause instanceof JsonParseException) {
            return getJsonParseErrorMessage();
        } else if (cause instanceof MismatchedInputException mie) {
            return getTypeMismatchErrorMessage(mie);
        } else if (cause instanceof JsonMappingException jme) {
            return getFieldProcessingErrorMessage(jme);
        }
        return getDefaultFormatErrorMessage();
    }
    
    private String createInvalidFormatExceptionMessage(InvalidFormatException ife) {
        if (ife.getTargetType() == null) {
            return getDefaultFormatErrorMessage();
        }
        
        String fieldName = extractFieldName(ife);
        
        if (ife.getTargetType().isEnum()) {
            return getEnumFormatErrorMessage(fieldName, ife.getValue(), ife.getTargetType());
        }
        
        if (Number.class.isAssignableFrom(ife.getTargetType()) || ife.getTargetType().isPrimitive()) {
            return getNumberFormatErrorMessage(fieldName, ife.getValue());
        }
        
        return getFieldFormatErrorMessage(fieldName, ife.getValue());
    }

    private String getDefaultFormatErrorMessage() {
        return messageUtils.getMessage("error.request.format.invalid");
    }

    private String getJsonParseErrorMessage() {
        return messageUtils.getMessage("error.json.parse");
    }

    private String getTypeMismatchErrorMessage(MismatchedInputException e) {
        return messageUtils.getMessage("error.field.type.mismatch", extractFieldName(e));
    }

    private String getFieldProcessingErrorMessage(JsonMappingException e) {
        return messageUtils.getMessage("error.field.processing", extractFieldName(e));
    }

    private String getEnumFormatErrorMessage(String fieldName, Object value, Class<?> enumClass) {
        return messageUtils.getMessage("error.field.enum.invalid", 
            fieldName, value, getEnumValues(enumClass));
    }

    private String getNumberFormatErrorMessage(String fieldName, Object value) {
        return messageUtils.getMessage("error.field.number.invalid", fieldName, value);
    }

    private String getFieldFormatErrorMessage(String fieldName, Object value) {
        return messageUtils.getMessage("error.field.format.invalid", fieldName, value);
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
        String message = e.getMessage() != null && e.getMessage().contains("PRODUCT_VIEW(ID") && e.getMessage().contains("CATEGORY")
            ? messageUtils.getMessage("error.product.duplicate.category")
            : messageUtils.getMessage("error.data.constraint.violation");

        HttpStatus status = message.equals(messageUtils.getMessage("error.product.duplicate.category"))
            ? HttpStatus.CONFLICT
            : HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = ErrorResponse.of(status.value(), message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(messageUtils.getMessage("error.internal.server"))
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 