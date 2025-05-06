package com.vietbevis.authentication.dto.response;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

  public static <T> ResponseEntity<ApiResponse<T>> toResponseEntity(ApiResponse<T> apiResponse) {
    HttpStatus httpStatus = HttpStatus.valueOf(apiResponse.getStatusCode());
    return ResponseEntity
        .status(httpStatus)
        .body(apiResponse);
  }

  public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
    return toResponseEntity(ApiResponse.success(data));
  }

  public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
    return toResponseEntity(ApiResponse.success(data, message));
  }

  public static <T> ResponseEntity<ApiResponse<T>> success(Integer statusCode,
      String message, T data) {
    return toResponseEntity(ApiResponse.success(statusCode, message, data));
  }

  public static <T> ResponseEntity<ApiResponse<T>> error(Integer statusCode,
      String message) {
    return toResponseEntity(ApiResponse.error(statusCode, message));
  }

  public static <T> ResponseEntity<ApiResponse<T>> validationError(
      List<ApiResponse.ValidationError> errors) {
    return toResponseEntity(ApiResponse.validationError(errors));
  }

  public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
    return toResponseEntity(ApiResponse.badRequest(message));
  }

  public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
    return toResponseEntity(ApiResponse.notFound(message));
  }
}
