package com.vietbevis.authentication.dto.request;

import java.io.Serial;
import java.io.Serializable;

import com.vietbevis.authentication.annotation.ValidEnum;
import com.vietbevis.authentication.common.VerificationCodeType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SendOtpRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Email(message = "Email không hợp lệ")
    @NotNull(message = "Email không được để trống")
    private String email;

    @NotNull(message = "Loại mã xác thực không được để trống")
    @ValidEnum(enumClass = VerificationCodeType.class)
    private String verificationCodeType;
}
