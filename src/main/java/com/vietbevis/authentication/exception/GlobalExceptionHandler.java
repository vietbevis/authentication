package com.vietbevis.authentication.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.vietbevis.authentication.dto.response.ApiResponse;
import com.vietbevis.authentication.dto.response.ResponseUtil;

import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(
            NoResourceFoundException ex) {
        return ResponseUtil.notFound(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        return ResponseUtil.badRequest("Lỗi tham số: " + ex.getName() + " không hợp lệ");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(
            NotFoundException ex) {
        return ResponseUtil.notFound(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(
            BadCredentialsException ex) {
        return ResponseUtil.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbiddenException(
            ForbiddenException ex) {
        return ResponseUtil.error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        return ResponseUtil.badRequest(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex) {
        return ResponseUtil.badRequest(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        List<ApiResponse.ValidationError> validationErrors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            validationErrors.add(ApiResponse.ValidationError.builder()
                    .field(fieldName)
                    .error(errorMessage)
                    .build());
        });

        return ResponseUtil.validationError(validationErrors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex) {
        List<ApiResponse.ValidationError> validationErrors = new ArrayList<>();

        ex.getAllErrors().forEach((error) -> {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();

                validationErrors.add(ApiResponse.ValidationError.builder()
                        .field(fieldName)
                        .error(errorMessage)
                        .build());
            } else {
                String errorMessage = error.getDefaultMessage();

                validationErrors.add(ApiResponse.ValidationError.builder()
                        .field("Unknown")
                        .error(errorMessage)
                        .build());
            }
        });

        return ResponseUtil.validationError(validationErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        List<ApiResponse.ValidationError> validationErrors = new ArrayList<>();

        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String paramName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            String errorMessage = violation.getMessage();
            validationErrors.add(ApiResponse.ValidationError.builder()
                    .field(paramName)
                    .error(errorMessage)
                    .build());
        });

        return ResponseUtil.validationError(validationErrors);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExistsException(
            AlreadyExistsException ex) {
        log.error("Email already exists error: {}", ex.getMessage());
        return ResponseUtil.error(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessagingException(MessagingException ex) {
        log.error("Email sending error: {}", ex.getMessage());
        return ResponseUtil.error(HttpStatus.BAD_REQUEST.value(), "Lỗi khi gửi email");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Đã xảy ra lỗi không mong muốn");
    }
}
