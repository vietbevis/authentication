package com.vietbevis.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietbevis.authentication.dto.request.LoginRequest;
import com.vietbevis.authentication.dto.request.RegisterRequest;
import com.vietbevis.authentication.dto.request.SendOtpRequest;
import com.vietbevis.authentication.dto.response.ApiResponse;
import com.vietbevis.authentication.dto.response.AuthResponse;
import com.vietbevis.authentication.dto.response.ResponseUtil;
import com.vietbevis.authentication.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Authentication", description = "Authentication API")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Đăng ký tài khoản", description = "Đăng ký tài khoản mới")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseUtil.success("Đăng ký thành công.");
    }

    @Operation(summary = "Gửi mã OTP", description = "Gửi mã OTP đến email của bạn")
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        authService.sendOTP(request);
        return ResponseUtil.success("Mã xác thực đã được gửi đến email của bạn.");
    }

    @Operation(summary = "Đăng nhập", description = "Đăng nhập vào hệ thống")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseUtil.success("Đăng nhập thành công.", authResponse);
    }

}
