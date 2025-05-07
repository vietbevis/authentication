package com.vietbevis.authentication.dto.request;

import java.io.Serial;
import java.io.Serializable;

import com.vietbevis.authentication.annotation.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotNull(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 50, message = "Mật khẩu phải có từ 8 đến 50 ký tự")
    @StrongPassword(message = "Mật khẩu yêu cầu phải có ít nhất 1 chữ cái viết hoa, 1 chữ cái viết thường, 1 số và 1 ký tự đặc biệt")
    private String password;
}
