package com.vietbevis.authentication.dto.request;

import com.vietbevis.authentication.annotation.MatchFields;
import com.vietbevis.authentication.annotation.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@MatchFields(
    password = "password",
    confirmPassword = "confirmPassword",
    fieldError = "confirmPassword",
    message = "Mật khẩu xác nhận không khớp với mật khẩu đã nhập"
)
public class RegisterRequest {

    @Size(min = 3, max = 50, message = "Tên phải có từ 3 đến 50 ký tự")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotNull(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 50, message = "Mật khẩu phải có từ 8 đến 50 ký tự")
    @StrongPassword(message = "Mật khẩu yêu cầu phải có ít nhất 1 chữ cái viết hoa, 1 chữ cái viết thường, 1 số và 1 ký tự đặc biệt")
    private String password;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String confirmPassword;

    @NotBlank(message = "OTP không được để trống")
    @Size(min = 6, max = 6, message = "OTP phải có 6 ký tự")
    private String otp;

}
