package com.vietbevis.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private Integer statusCode;

    private boolean success;

    private String message;

    private T data;

    private List<ValidationError> errors;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
            .statusCode(HttpStatus.OK.value())
            .success(true)
            .message("Thành công.")
            .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .statusCode(HttpStatus.OK.value())
            .success(true)
            .message("Thành công.")
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .statusCode(HttpStatus.OK.value())
            .success(true)
            .message(message)
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> success(Integer statusCode, String message,
        T data) {
        return ApiResponse.<T>builder()
            .statusCode(statusCode)
            .success(true)
            .message(message)
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> error(Integer statusCode, String message) {
        return ApiResponse.<T>builder()
            .statusCode(statusCode)
            .success(false)
            .message(message)
            .build();
    }

    public static <T> ApiResponse<T> validationError(List<ValidationError> errors) {
        return ApiResponse.<T>builder()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .success(false)
            .message("Lỗi xác thực. Vui lòng kiểm tra lại dữ liệu đầu vào.")
            .errors(errors)
            .build();
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST.value(), message);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(HttpStatus.NOT_FOUND.value(), message);
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {

        private String field;
        private String error;
    }
}
