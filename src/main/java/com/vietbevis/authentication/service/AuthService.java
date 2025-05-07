package com.vietbevis.authentication.service;

import com.vietbevis.authentication.dto.request.RegisterRequest;
import com.vietbevis.authentication.dto.request.SendOtpRequest;

public interface AuthService {
    void register(RegisterRequest request);

    void sendOTP(SendOtpRequest request);
}
