package com.vietbevis.authentication.service;

import com.vietbevis.authentication.dto.request.LoginRequest;
import com.vietbevis.authentication.dto.request.RegisterRequest;
import com.vietbevis.authentication.dto.request.SendOtpRequest;
import com.vietbevis.authentication.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);

    void sendOTP(SendOtpRequest request);

    AuthResponse login(LoginRequest request);
}
